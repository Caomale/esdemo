package com.eyevision.esearch.mapper;

import com.eyevision.esearch.entity.Goods;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Description 商品数据库持久层接口
 * @Author caoxb
 * @since 2019-07-19 15:22
 */
public interface GoodsMapper extends ElasticsearchRepository<Goods, Long> {

    /**
     * 使用JPA语法 自定义 方法
     */

    public List<Goods> findByPriceBetween(double pc1, double pc2);
}
