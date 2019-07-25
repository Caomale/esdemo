package com.eyevision.esearch.service;

import com.eyevision.esearch.entity.Goods;
import com.eyevision.esearch.model.GoodsFrom;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * @Description 商品业务层接口类
 * @Author caoxb
 * @since 2019-07-23 11:33
 */
public interface GoodsService {
    /**
     * 分页查询
     * @return
     */
    Page<Goods> searchPage(GoodsFrom from);

    /**
     * 模糊查询
     * @param title
     * @return
     */
    List<Goods> findByTitle(String title);
}
