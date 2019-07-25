package com.eyevision.esearch.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @Description 题目实体类
 * @Author caoxb
 * @since 2019-07-24 13:42
 */
@Data
public class Article implements Serializable {

    private Long tId;
    private String title;
    private String author;
    private String content;
    private String publishDate;


}
