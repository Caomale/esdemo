package com.eyevision.esearch.estest;

import com.eyevision.esearch.EsearchApplicationTests;
import com.eyevision.esearch.entity.Article;
import com.eyevision.esearch.service.ArticleService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description 测试使用Jest 操作elasticsearch
 * @Author caoxb
 * @since 2019-07-24 10:56
 */
@Slf4j
public class testJest extends EsearchApplicationTests {

    @Autowired
    JestClient jestClient;

    @Autowired
    ArticleService articleService;

    @Test
    public void testJestCreteIndex() throws IOException {
        //创建索引
        jestClient.execute(new CreateIndex.Builder("es_article").build());
        //创建 type
        PutMapping.Builder builder = new PutMapping.Builder("es_article", "articles", "ArticleMappings");
        JestResult jestResult = jestClient.execute(builder.build());
        if (!jestResult.isSucceeded()) {
            //失败
            log.info("【创建索引:{失败！}...】", "es_article");
        }
        log.info("【创建索引:{Successful！}...】", "es_article");
    }

    /**
     * 获取索引
     *
     * @throws IOException
     */
    @Test
    public void getJestIndex() throws IOException {
        GetMapping.Builder builder = new GetMapping.Builder();
        builder.addIndex("es_article").addType("articles");
        JestResult result = jestClient.execute(builder.build());
        System.out.println("testGetJestIndex：" + result.getJsonObject());
    }

    /**
     * 新增索引数据
     *
     * @throws SQLException
     * @throws IOException
     */
    @Test
    public void add() throws SQLException, IOException {
        Article article = new Article();
        article.setTitle("三国演义");
        article.setAuthor("吴孟达");
        article.setContent("三国演义是中国文学的四大名著之一，里面的人物形象表现得的十分淋漓尽致，体现了当时人们生活在战乱的疾苦！");
        article.setPublishDate("2019-07-08 14:54:49");
        boolean result = articleService.add(article);

        System.out.println("result：" + result);

        Index.Builder builder = new Index.Builder(article);
        builder.id("sg001");
        builder.refresh(true);
        Index index = builder.index("es_article").type("articles").build();
        JestResult jr = jestClient.execute(index);
        if (jr != null && !jr.isSucceeded()) {
            throw new RuntimeException(jr.getErrorMessage() + "插入更新索引失败!");
        }
    }

    /**
     * 删除索引数据
     *
     * @throws IOException
     */
    @Test
    public void testDelIndex() throws IOException {
        Delete.Builder builder = new Delete.Builder("红楼梦");
        builder.refresh(true);
        Delete delete = builder.index("es_article").type("articles").build();
        JestResult result = jestClient.execute(delete);
        if (result != null && !result.isSucceeded()) {
            throw new RuntimeException(result.getErrorMessage() + "删除文档失败!");
        }

    }

    /**
     * 批量新增索引数据
     *
     * @throws SQLException
     * @throws IOException
     */
    @Test
    public void testFindByAll() throws SQLException, IOException {
        List<Article> articles = articleService.findByAll();

        if (articles != null) {
            for (Article article : articles) {
                System.out.println("result：" + article);

                Index.Builder builder = new Index.Builder(article);
                builder.id("ac_" + article.getTId().toString());
                builder.refresh(true);
                Index index = builder.index("es_article").type("articles").build();
                JestResult jr = jestClient.execute(index);
                if (jr != null && !jr.isSucceeded()) {
                    throw new RuntimeException(jr.getErrorMessage() + "插入更新索引失败!");
                }
            }
        }
    }

    @Test
    public void testSearchId() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","三国"));
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("es_article").addType("articles").build();
        if (search != null){
            JestResult jestResult = jestClient.execute(search);
            log.info("获取ES信息成功！");
            System.out.println("jestResult：" + jestResult.getJsonObject());
        } else {
            log.error("获取ES信息异常.....");
        }
    }

    /**
     * 多条件查询
     * @throws IOException
     */
    @Test
    public void testSearchAll() throws IOException {
        Article article = new Article();
        article.setTId(02L);
        article.setPublishDate("");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            if (!article.getPublishDate().isEmpty() && article.getPublishDate() != null) {
                queryBuilder.must(QueryBuilders.rangeQuery("publishDate").gte(article.getPublishDate()));
            }
            if (!article.getPublishDate().isEmpty()) {
                queryBuilder.must(QueryBuilders.rangeQuery("publishDate").lte(article.getPublishDate()));
            }
            if (!article.getTitle().isEmpty() && article.getTitle() != null) {
                queryBuilder.must(QueryBuilders.matchQuery("title", article.getTitle()));
            }
            if (article.getTId() != null) {
                queryBuilder.must(QueryBuilders.matchQuery("tId", article.getTId()));
            }
            searchSourceBuilder.query(queryBuilder);
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("es_article").addType("articles").build();
            JestResult jestResult = jestClient.execute(search);
            System.out.println("searchResult：" + jestResult.getJsonObject());
        }
    }
