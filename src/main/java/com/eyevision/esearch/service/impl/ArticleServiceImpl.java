package com.eyevision.esearch.service.impl;

import com.eyevision.esearch.conf.MysqlJdbcFactory;
import com.eyevision.esearch.entity.Article;
import com.eyevision.esearch.service.ArticleService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description Article接口实现类
 * @Author caoxb
 * @since 2019-07-24 14:14
 */
@Component
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private MysqlJdbcFactory jdbcFactory;

    private Connection con;
    private PreparedStatement pstmt;
    private ResultSet res;

    @Override
    public Boolean add(Article article) throws SQLException {
        boolean status;
        String sql = "insert into es_article (title, author, content, publishDate) values(?,?,?,?)";
        con = jdbcFactory.getConnect();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getAuthor());
            pstmt.setString(3, article.getContent());
            pstmt.setString(4, article.getPublishDate());
            status = pstmt.execute();

        jdbcFactory.getCloseAll(con, pstmt, res);

        return status;

    }

    @Override
    public void update(Article article) {

    }

    @Override
    public void del(Long id) {

    }

    @Override
    public List<Article> findByAll() throws SQLException {
        List<Article> list = new ArrayList<>();
        String sql = "select * from es_article";
        con = jdbcFactory.getConnect();
        pstmt = con.prepareStatement(sql);
        res = pstmt.executeQuery();
        while (res.next()){
            Article article = new Article();
            article.setTId(res.getLong("tId"));
            article.setTitle(res.getString("title"));
            article.setAuthor(res.getString("author"));
            article.setContent(res.getString("content"));
            article.setPublishDate(res.getString("publishDate"));
            list.add(article);
        }
        jdbcFactory.getCloseAll(con, pstmt, res);

        return list;
    }
}
