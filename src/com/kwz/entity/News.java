package com.kwz.entity;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.kwz.enums.NewsType;
import com.kwz.util.KwzUtil;

//@Entity
public class News extends KwzBaseBean {

    private static final long serialVersionUID = 1L;

    private NewsType newsType;
    private int hits;
    private String author;
    @NotNull @NotEmpty
    private String content;
    private Date contentUpdated;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

//    @Transient
    public String getContentInHtml() {
        return KwzUtil.textToHtml(content);
    }

    public Date getContentUpdated() {
        return contentUpdated;
    }

    public void setContentUpdated(Date contentUpdated) {
        this.contentUpdated = contentUpdated;
    }

    public NewsType getNewsType() {
        return newsType;
    }

    public void setNewsType(NewsType newsType) {
        this.newsType = newsType;
    }

    public void newHit() {
        hits++;

    }

}
