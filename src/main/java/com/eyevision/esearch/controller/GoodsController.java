package com.eyevision.esearch.controller;

import com.eyevision.esearch.entity.Goods;
import com.eyevision.esearch.mapper.GoodsMapper;
import com.eyevision.esearch.model.GoodsFrom;
import com.eyevision.esearch.service.GoodsService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 商品控制层类
 * @Author caoxb
 * @since 2019-07-23 11:22
 */

@Api(tags = "商品管理")
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsService goodsService;

    @PostMapping("/insert")
    public void add(@RequestBody List<Goods> goods){
        goodsMapper.saveAll(goods);
    }

    @DeleteMapping("/{id}/del")
    public void delGoods(@PathVariable Long id){
        goodsMapper.deleteById(id);
    }

    @PutMapping("/update")
    public void update(@RequestBody Goods goods){
        goodsMapper.save(goods);
    }

    @GetMapping("/search")
    public Page<Goods> searchPage(@RequestBody GoodsFrom from){
        Page<Goods> goods = goodsService.searchPage(from);
        return goods;
    }

}
