package com.shop.custom.entity;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private Long id;
	private String name;
	private List<Product> products = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Category{" +
				"id=" + id +
				", name='" + name + '\'' +
				", products=" + products +
				'}';
	}
}
