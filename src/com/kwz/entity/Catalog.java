package com.kwz.entity;


import java.util.ArrayList;
import java.util.List;

//@Entity
public class Catalog extends KwzBaseBean  {

	private static final long serialVersionUID = 1L;
	
    private List<Product> products = new ArrayList<Product>();
	

//    @OneToMany(mappedBy = "catalog", cascade=CascadeType.ALL)
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public void addProduct(Product product) {
		this.products.add(product);
	}

	
	

}
