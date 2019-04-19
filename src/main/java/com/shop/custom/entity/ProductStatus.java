package com.shop.custom.entity;

public enum ProductStatus {
	AVAILABLE("Available"),
	ABSENT("Absent"),
	EXPECTED("Expected");

	private final String status;

	ProductStatus(String status){
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static ProductStatus fromString(String status) {
		for (ProductStatus ps : ProductStatus.values()) {
			if (ps.status.equalsIgnoreCase(status)) {
				return ps;
			}
		}
		return null;
	}
}
