package com.eyevision.esearch.estest;

import com.eyevision.esearch.EsearchApplicationTests;
import com.eyevision.esearch.entity.Goods;
import com.eyevision.esearch.mapper.GoodsMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

/**
 * @Description ES基本测试
 * @Author caoxb
 * @since 2019-07-18 21:06
 */
public class ESTest extends EsearchApplicationTests {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    public void testCreatIndex() {
        esTemplate.createIndex(Goods.class);
    }

    @Test
    public void testDelIndex() {
        esTemplate.deleteIndex(Goods.class);
    }

    @Test
    public void testInsert() {
        // 单个新增
        Goods goods = new Goods(1L, "努比亚X", " 手机",
                "努比亚", 3299.00, "http://image.baidu.com/13123.jpg");
        goodsMapper.save(goods);
    }

    @Test
    public void testInsertList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(6L, "苹果RX", " 手机", "苹果", 6699.00, "http://consumer.gucheng.com/UploadFiles_6578/201811/2018113014245308.jpg"));
        list.add(new Goods(4L, "一加7Pro", " 手机", "一加", 4499.00, "http://img.sccnn.com/bimg/341/08208.jpg"));
        list.add(new Goods(5L, "华为META20", " 手机", "华为", 5499.00, "http://img2.imgtn.bdimg.com/it/u=1296553222,4210686173&fm=214&gp=0.jpg"));

        goodsMapper.saveAll(list);
    }

    /**
     * 注意：elasticsearch 中更新 与 新增 是同一个方法，底层原理是有两种，一是将原有的值直接覆盖，二是删除原有插入更新的值
     */
    @Test
    public void testUpdate() {
        Goods goods = new Goods(1L, "努比亚红魔", " 手机",
                "努比亚", 3299.00, "http://image.baidu.com/3658346521.jpg");
        goodsMapper.save(goods);
    }

    @Test
    public void testFindBy() {
        Optional<Goods> goods = goodsMapper.findById(6L);
        System.out.println("testFindById（）：" + goods.toString());

        System.out.println("****----------*******");
        Iterable<Goods> goodsList = goodsMapper.findAll(Sort.by("price").descending());  // 按价格 升序
        for (Goods g :
                goodsList) {
            System.out.println("testFindAll():" + g.toString());
        }
    }

    @Test
    public void testJPASelfMath() {
        List<Goods> list = goodsMapper.findByPriceBetween(5499.00d, 8000d);
        for (Goods g :
                list) {
            System.out.println("testJPASelfMath：" + g.toString());
        }
    }

    /**
     * 一下都是 自定义 查询
     */
    // matchQuery 方法查询
    @Test
    public void testMatchQuery() {
        // 构造查询器
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        // 添加 基本分词查询
        searchQuery.withQuery(QueryBuilders.matchQuery("title", "华为"));
        // Boolean类型 ： 判断这个手机是否属于那个品牌
        searchQuery.withQuery(
                QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title", "华为"))
                        .must(QueryBuilders.matchQuery("brand", "华为"))
        );

        // 结果
        // Iterable<Goods> goods = goodsMapper.search(searchQuery.build());
        Page<Goods> page = goodsMapper.search(searchQuery.build());
        long total = page.getTotalElements();
        System.out.println("page.getTotalElements() = " + total);
        for (Goods g :
                page) {
            System.out.println("testMatchQuery " + g);
        }
    }

    // termQuery() 方法 支持的数据类型更多（int、float、double、long等等）
    @Test
    public void testTermQuery() {
        // 构造器
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        // 构造条件
        builder.withQuery(QueryBuilders.termQuery("price", 5499.00));
        // 查询
        Page<Goods> goods = goodsMapper.search(builder.build());
        // 页码 条数
        long total = goods.getTotalElements();
        int page = goods.getTotalPages();
        System.out.println("条数：" + total + "，页数：" + page);
        for (Goods g :
                goods) {
            System.out.println("goods : " + g);
        }
    }

    // 模糊查询 - fuzzyQuery() 与 布尔查询 - boolQuery()
    @Test
    public void testLikes() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        // 构造条件 fuzzyQuery() 模糊查询
        // builder.withQuery(QueryBuilders.fuzzyQuery("title", "果"));
        // 布尔查询
        builder.withQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title", "华为")).must(QueryBuilders.matchQuery("brand", "华为")));
        Page<Goods> goods = goodsMapper.search(builder.build());
        for (Goods g :
                goods) {
            // System.out.println("this is test fuzzyQuery()：" + g);
            System.out.println("this is test boolQuery()：" + g);
        }
    }

    @Test
    public void testSearchPage() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 构造查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("category", "手机"));

        // 排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        // 自定义分页
        int page = 0;
        int size = 2;
        queryBuilder.withPageable(PageRequest.of(page, size));

        Page<Goods> goods = goodsMapper.search(queryBuilder.build());
        // 总条数
        System.out.println("总条数total：" + goods.getTotalElements());
        // 总页数
        System.out.println("总页数pages：" + goods.getTotalPages());
        //
        System.out.println("当前页：" + goods.getNumber());
        System.out.println("页面大小：" + goods.getSize());

        for (Goods g :
                goods) {
            System.out.println("实现分页查询结果：" + g);
        }
    }

    // Es 中“聚合”特性的使用 之 “桶(Bucket)” 与 “度量(Aggregations)”
    @Test
    public void testBucket() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        builder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        builder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Goods> apage = (AggregatedPage<Goods>) goodsMapper.search(builder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms terms = (StringTerms) apage.getAggregation("brands");
        // 4. 获取桶
        List<StringTerms.Bucket> buckets = terms.getBuckets();
        for (StringTerms.Bucket b :
                buckets) {
            System.out.println("桶的Key为：" + b.getKey());
            System.out.println("桶中的文档数：" + b.getDocCount());
        }
    }

    @Test
    public void testSubAggs() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        builder.addAggregation(AggregationBuilders.terms("brands").field("brand").subAggregation(AggregationBuilders.avg("priceAvg").field("price")));

        AggregatedPage<Goods> page = (AggregatedPage<Goods>) goodsMapper.search(builder.build());

        StringTerms terms = (StringTerms) page.getAggregation("brands");

        List<StringTerms.Bucket> buckets = terms.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");

            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());
        }
    }

    @Test
    public void testBucketAndAggre() {
        //目标：搜索写博客写得最多的用户（一个博客对应一个用户），通过搜索博客中的用户名的频次来达到想要的结果
        //首先新建一个用于存储数据的集合
        List<String> ueserNameList = new ArrayList<>();
        //1.创建查询条件，也就是QueryBuild
        QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();//设置查询所有，相当于不设置查询条件
        //2.构建查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2.0 设置QueryBuilder
        queryBuilder.withQuery(matchAllQuery);
        //2.1设置搜索类型，默认值就是QUERY_THEN_FETCH，参考https://blog.csdn.net/wulex/article/details/71081042
        queryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);//指定索引的类型，只先从各分片中查询匹配的文档，再重新排序和排名，取前size个文档
        //2.2指定索引库和文档类型
        queryBuilder.withIndices("goods").withTypes("docs");//指定要查询的索引库的名称和类型，其实就是我们文档@Document中设置的indedName和type
        //2.3重点来了！！！指定聚合函数,本例中以某个字段分组聚合为例（可根据你自己的聚合查询需求设置）
        //该聚合函数解释：计算该字段(假设为brand)在所有文档中的出现频次，并按照降序排名（常用于某个字段的热度排名）
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("goodsBrand").field("brand");
        queryBuilder.addAggregation(termsAggregation);
        //2.4构建查询对象
        NativeSearchQuery nativeSearchQuery = queryBuilder.build();
        //3.执行查询
        //3.1方法1,通过reporitory执行查询,获得有Page包装了的结果集
        Page<Goods> search = goodsMapper.search(nativeSearchQuery);
        List<Goods> content = search.getContent();
        for (Goods g : content) {
            ueserNameList.add(g.getTitle());
        }
        //获得对应的Goods之后我就可以获得该文档的品牌，那么就可以查出最热门手机品牌了
        //3.2方法2,通过elasticSearch模板elasticsearchTemplate.queryForList方法查询
        List<Goods> queryForList = esTemplate.queryForList(nativeSearchQuery, Goods.class);
        //3.3方法3,通过elasticSearch模板elasticsearchTemplate.query()方法查询,获得聚合(常用)
        Aggregations aggregations = esTemplate.query(nativeSearchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //转换成map集合
        Map<String, Aggregation> aggregationMap = aggregations.asMap();
        //获得对应的聚合函数的聚合子类，该聚合子类也是个map集合,里面的value就是桶Bucket，我们要获得Bucket
        StringTerms stringTerms = (StringTerms) aggregationMap.get("goodsBrand");
        //获得所有的桶
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        //将集合转换成迭代器遍历桶,当然如果你不删除buckets中的元素，直接foreach遍历就可以了
        Iterator<StringTerms.Bucket> iterator = buckets.iterator();

        while (iterator.hasNext()) {
            //bucket桶也是一个map对象，我们取它的key值就可以了
            String username = iterator.next().getKeyAsString();//或者bucket.getKey().toString();
            //根据username去结果中查询即可对应的文档，添加存储数据的集合
            ueserNameList.add(username);
        }
        //最后根据ueserNameList搜索对应的结果集
        // List<Goods> listUsersByUsernames = userService.listUsersByUsernames(ueserNameList);
    }


}
