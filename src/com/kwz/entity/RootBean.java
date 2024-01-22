package com.kwz.entity;

import java.util.List;

public class RootBean extends KwzBaseBean {
    private static final long serialVersionUID = -3328794640074590839L;
    List<Catalog> catalogs;
    List<News> news;
    List<User> users;

    public RootBean() {
        name = "ALL";
    };

    public RootBean(List<Catalog> catalogs, List<News> news, List<User> users) {
        this();
        this.catalogs = catalogs;
        this.news = news;
        this.users = users;
    }

    public String getId() {
        return null;
    }

    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
