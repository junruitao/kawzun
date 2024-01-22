package com.kwz.service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.kwz.entity.Catalog;
import com.kwz.entity.News;
import com.kwz.entity.Product;
import com.kwz.entity.User;
import com.kwz.enums.EntityType;

public interface KwzDao extends Serializable{
	public List<Catalog> getCatalogs();
	public List<News> getNews();
	public Catalog newCatalog();
	public Product newProduct();
	public News newNews();
//	public User getUser(String name);
	public List<User> getUsers();
//    public void addProduct(Catalog cat, Product p);
//    public void addCatalog(Catalog c);
//    public void adjustAll();
//    public void removeProduct(Catalog catalog, Product o);
//    public void removeCatalog(Catalog o);
//    public void removeNews(News o);
//    public <T extends IdedTimestampedBase> void update(T entity);
    @RolesAllowed("ROLE_ADMIN")
    public void setChanged(EntityType news);
    public User newUser();
}
