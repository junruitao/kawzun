package com.kwz.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kwz.aws.KwzStorageObject;
import com.kwz.aws.S3StorageManager;
import com.kwz.entity.Catalog;
import com.kwz.entity.News;
import com.kwz.entity.Product;
import com.kwz.entity.User;
import com.kwz.enums.EntityType;
import com.kwz.enums.NewsType;
import com.kwz.util.KwzUtil;

@Service("kwzDao")
public class KwzS3DaoImpl implements DisposableBean, Runnable, KwzDao {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(KwzS3DaoImpl.class.getName());
    List<Catalog> catalogs;
    List<News> news;
    List<User> users;
    private boolean bNewsChanged;
    private boolean bCatalogChanged;
    private boolean bUserChanged;

    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public Catalog newCatalog() {
        Catalog cat = new Catalog();
        cat.setName("New cat");
        return cat;
    }

    public Product newProduct() {
        Product p = new Product();
        p.setName("New Product");
        return p;
    }

    @Override
    public List<News> getNews() {
        return news;
    }

    @Override
    public News newNews() {
        News news = new News();
        news.setNewsType(NewsType.company);
        news.setName("title_ new");
        news.setContent("contnent_new");
        return news;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    @Override
    public User newUser() {
        User newUser = new User();
        return newUser;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

//    @Override
//    public User getUser(String name) {
//        User user = new User();
//        user.setName("admin");
//        user.setPassword("");
//        return user;
//    }

    @Override
    public void setChanged(EntityType type) {
        switch (type) {
        case News:
            bNewsChanged = true;
            break;
        case Catalog:
            bCatalogChanged = true;
            break;
        case User:
            bUserChanged = true;
            break;
        }

    }

    // a fixed period between invocations in milliseconds
    private static final int FLUSH_RATE = 10 * 60 * 1000;

    @Scheduled(fixedRate = FLUSH_RATE)
    public void run() {
        logger.info("Invoked by Timer");
        try {
            flush(false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Invoked when bean destroyed
     */
    public void destroy() throws Exception {
        logger.info("Invoked by shutdown event.");
        flush(false);
    }

    public void flush(boolean force) throws IOException {
        if (force)
            bCatalogChanged = bUserChanged = bNewsChanged = true;
        if (!bCatalogChanged &&!bUserChanged &&!bNewsChanged ){
            logger.info("Nothing to change!");
            return;
        }

        S3StorageManager mgr = new S3StorageManager();
        if (bCatalogChanged) {
            KwzStorageObject obj = new KwzStorageObject(EntityType.Catalog);
            obj.setData(KwzUtil.getBytes(EntityType.Catalog, (Serializable) catalogs));
            mgr.store(obj, false, null);
            logger.info("Catalog changed! Flushed");
        }
        if (bUserChanged) {
            KwzStorageObject obj = new KwzStorageObject(EntityType.User);
            obj.setData(KwzUtil.getBytes(EntityType.User, (Serializable) users));
            mgr.store(obj, false, null);
            logger.info("User changed! Flushed");
        }
        if (bNewsChanged) {
            KwzStorageObject obj = new KwzStorageObject(EntityType.News);
            obj.setData(KwzUtil.getBytes(EntityType.News, (Serializable) news));
            mgr.store(obj, false, null);
            logger.info("News changed! Flushed");
        }
        // KwzStorageObject obj = new KwzStorageObject(EntityType.Root);
        // obj.setData(KwzUtil.getBytes(EntityType.Root, new RootBean(catalogs,news,users)));
        // mgr.store(obj, false, null);

        bCatalogChanged = bUserChanged = bNewsChanged = false;

    }

    @PostConstruct
    public void load() throws IOException {
        S3StorageManager mgr = new S3StorageManager();
        KwzStorageObject obj = new KwzStorageObject(EntityType.Catalog);
        InputStream is = mgr.loadInputStream(obj);
        catalogs = (List<Catalog>) KwzUtil.getEntities(EntityType.Catalog, is);

        obj = new KwzStorageObject(EntityType.News);
        is = mgr.loadInputStream(obj);
        news = (List<News>) KwzUtil.getEntities(EntityType.News, is);

        obj = new KwzStorageObject(EntityType.User);
        is = mgr.loadInputStream(obj);
        users = (List<User>) KwzUtil.getEntities(EntityType.User, is);

        // obj = new KwzStorageObject(EntityType.Root);
        // is = mgr.loadInputStream(obj);
        // RootBean r = (RootBean) KwzUtil.getEntities(EntityType.Root, is);

        bCatalogChanged = bUserChanged = bNewsChanged = false;
    }

    // // update a single entity without changing the order
    // public <T extends IdedTimestampedBase> void update(T e) {
    // }
    //
    // @Override
    // public void addProduct(Catalog cat, Product p) {
    // cat.addProduct(p);
    // }
    //
    // @Override
    // public void addCatalog(Catalog c) {
    // catalogs.add(c);
    // }
    //
    // @Override
    // public void adjustAll() {
    // }
    //
    // @Override
    // public void removeProduct(Catalog catalog, Product o) {
    // catalog.getProducts().remove(o);
    // }
    //
    // @Override
    // public void removeCatalog(Catalog o) {
    // getCatalogs().remove(o);
    // }
    //
    // @Override
    // public void removeNews(News o) {
    // getNews().remove(o);
    // }
}
