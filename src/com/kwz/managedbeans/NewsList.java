package com.kwz.managedbeans;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.kwz.entity.News;
import com.kwz.service.KwzDao;

@ManagedBean(name = "newsList")
@ViewScoped
public class NewsList implements Serializable {

    private static final long serialVersionUID = 8957439343830408210L;
    private static final int MAX_TOP_NEWS = 7;
    private static final int PAGE_SIZE = 10;
    private static Logger logger = Logger.getLogger(NewsList.class.getName());

    private News currentNews;

    private String newsId;

    @ManagedProperty("#{kwzDao}")
    private KwzDao kwzDao;

    private int page;
    private News previous;
    private News next;

    public List<News> getAllNews() {
        return kwzDao.getNews();

    }

    public KwzDao getKwzDao() {
        return kwzDao;
    }

    public void setKwzDao(KwzDao kwzDao) {
        this.kwzDao = kwzDao;
    }

    // top news for home page
    public List<News> getTopNews() {
    	List<News> topNews = getAllNews().subList(0, Math.min(MAX_TOP_NEWS, getAllNews().size()));
        return topNews;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        int newPage = Math.max(0, Math.min(page, getTotalPages() - 1));
        this.page = newPage;
    }

    public int getTotalPages() {
        return (getAllNews().size() + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    // top news for home page
    public List<News> getFilteredNewsByPage() {
        return getAllNews().subList(page * PAGE_SIZE, Math.min((page + 1) * PAGE_SIZE, getAllNews().size()));
    }

    public String getNewsId() {
        return newsId;
    }

    // For news detail
    public void setNewsId(String newsId) {
        if (newsId != null) {
            this.newsId = newsId;
            currentNews = null;
            previous = next = null;
            for (News n : getAllNews()) {
                if (currentNews == null) {
                    if (n.getId().equals(newsId)) {
                        currentNews = n;
                        currentNews.newHit();
                    } else
                        previous = n;

                } else {
                    next = n;
                    break;
                }
            }
            if (currentNews == null)
                previous = next = null;
        }
    }

    public News getCurrentNews() {
        return currentNews;
    }

    public void setCurrentNews(News currentNews) {
        this.currentNews = currentNews;
    }

    public News getPrevious() {
        return previous;
    }

    public void setPrevious(News previous) {
        this.previous = previous;
    }

    public News getNext() {
        return next;
    }

    public void setNext(News next) {
        this.next = next;
    }

    public int getPrevPage() {
        return page - 1;
    }

    public boolean hasPrevPage() {
        return page > 1;
    }

    public int getNextPage() {
        return page + 1;
    }

    public boolean hasNextPage() {
        return page < getTotalPages() - 1;
    }

}
