package com.eyevision.esearch.model;

/**
 * @Description 商品查询视图模型
 * @Author caoxb
 * @since 2019-07-23 17:05
 */
public class GoodsFrom {

    private String title; //标题
    private String category;// 分类
    private String brand; // 品牌
    private Double price; // 价格

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
