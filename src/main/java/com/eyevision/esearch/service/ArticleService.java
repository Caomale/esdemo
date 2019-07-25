package com.eyevision.esearch.service;

import com.eyevision.esearch.entity.Article;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description Article业务接口
 * @Author caoxb
 * @since 2019-07-24 14:11
 */
public interface ArticleService {

    Boolean add(Article article) throws SQLException;

    void update(Article article);

    void del(Long id);

    List<Article> findByAll() throws SQLException;

}
