package com.kwz.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import com.kwz.entity.Catalog;
import com.kwz.entity.Product;
import com.kwz.service.KwzDao;

@ManagedBean(name = "productList")
@ViewScoped
public class ProductList implements Serializable {

	private static final long serialVersionUID = 8957439343830408210L;
	private static final int MAX_POPULAR_PRODUCTS = 7;
	private static final int MAX_DISCOUNT_PRODUCTS = 2;
	private static Logger logger = Logger
			.getLogger(ProductList.class.getName());

	private List<Catalog> catalogs;
	private List<Product> popularProducts;
	private List<Product> discountProducts; 

	@ManagedProperty("#{kwzDao}")
	private KwzDao kwzDao;

	// private String catalogId;
	// private String productId;
	private Catalog currentCatalog;
	private Product currentProduct;

	@PostConstruct
	public void listCatalogs() {
		catalogs = kwzDao.getCatalogs();
		if (currentCatalog == null && catalogs.size() > 0) {
			currentCatalog = catalogs.get(0);
			// catalogId = currentCatalog.getId();
		}
		if (currentProduct == null && currentCatalog.getProducts().size() > 0) {
			currentProduct = currentCatalog.getProducts().get(0);
			// productId = currentProduct.getId();
		}
		logger.info("Found " + catalogs.size() + " catalogs");

	}

	public KwzDao getKwzDao() {
		return kwzDao;
	}

	public void setKwzDao(KwzDao kwzDao) {
		this.kwzDao = kwzDao;
	}

	public List<Catalog> getCatalogs() {
		return catalogs;
	}

	public String getCatalogId() {
		if (currentCatalog != null)
			return currentCatalog.getId();
		return "";
	}

	public void setCatalogId(String catalogId) {
		for (Catalog c : catalogs)
			if (catalogId.equals(c.getId())) {
				currentCatalog = c;
				break;
			}
	}

	public String getProductId() {
		if (currentProduct != null)
			return currentProduct.getId();
		return "";
	}

	public void setProductId(String productId) {
		if (!StringUtils.isBlank(productId)) {
			for (Catalog c : catalogs)
				for (Product p : c.getProducts())
					if (productId.equals(p.getId())) {
						currentCatalog = c;
						currentProduct = p;
						break;
					}
		}
	}

	public Catalog getCurrentCatalog() {
		return currentCatalog;
	}

	public Product getCurrentProduct() {
		return currentProduct;
	}

	public Product getNextProduct() {
		if (currentProduct != null) {
			boolean located = false;
			for (Catalog c : catalogs)
				for (Product p : c.getProducts()) {
					if (located)
						return p;
					if (currentProduct.equals(p))
						located = true;

				}
		}
		return null;
	}

	public Product getPreviousProduct() {
		if (currentProduct != null) {
			Product prev = null;
			for (Catalog c : catalogs)
				for (Product p : c.getProducts()) {
					if (currentProduct.equals(p))
						return prev;
					prev = p;
				}
		}
		return null;
	}

	public List<Product> getPopularProducts() {
		if (popularProducts == null) {
			popularProducts = new ArrayList<Product>();
			for (Catalog c : catalogs)
				for (Product p : c.getProducts()) {
					if (p.isPopular()) {
						popularProducts.add(p);
						if (popularProducts.size() >= MAX_POPULAR_PRODUCTS)
							return popularProducts;

					}
				}
			for (Catalog c : catalogs)
				for (Product p : c.getProducts()) {
					if (popularProducts.size() >= MAX_POPULAR_PRODUCTS)
						return popularProducts;
					popularProducts.add(p);
				}
		}
		return popularProducts;
	}

	public List<Product> getDiscountProducts() {
		if (discountProducts == null) {
			discountProducts = new ArrayList<Product>();
			for (Catalog c : catalogs)
				for (Product p : c.getProducts()) {
					if (p.isDiscount()) {
						discountProducts.add(p);
						if (discountProducts.size() >= MAX_DISCOUNT_PRODUCTS)
							return discountProducts;

					}
				}
		}
		return discountProducts;
	}

}
