package com.shop.custom.entity;

public class Product {
	private Long id;
	private String title;
	private Double price;
	private String status;

	public Product() {
	}

	public Product(String title, Double price, ProductStatus status) {
		this.title = title;
		this.price = price;
		this.status = status.getStatus();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = ProductStatus.fromString(status).getStatus();
	}

	@Override
	public String toString() {
		return "Product{" +
				"id=" + id +
				", title='" + title + '\'' +
				", price=" + price +
				", status='" + status + '\'' +
				'}';
	}
}
