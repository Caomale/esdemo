package com.eyevision.esearch.service.impl;

import com.eyevision.esearch.entity.Goods;
import com.eyevision.esearch.mapper.GoodsMapper;
import com.eyevision.esearch.model.GoodsFrom;
import com.eyevision.esearch.service.GoodsService;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

/**
 * @Description 商品业务实现类
 * @Author caoxb
 * @since 2019-07-23 11:35
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Page<Goods> searchPage(GoodsFrom from) {

    return null;
    }

    @Override
    public List<Goods> findByTitle(String title) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.fuzzyQuery("title", title));
        Iterable<Goods> goods = goodsMapper.search(queryBuilder.build());
        return null;
    }
}
