package com.shop.custom;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shop.custom.entity.Product;
import com.shop.custom.entity.ProductStatus;
import com.shop.custom.entity.Shop;
import com.shop.custom.factory.ShopFactory;

public class Main {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) {
		Shop rozetka = ShopFactory.getShop("Rozetka");
		Shop comfy = ShopFactory.getShop("Comfy");

		if(Objects.nonNull(comfy)) {
			List<Product> comfyProducts = Arrays.asList(
				new Product("Lenovo G500",10000.0,ProductStatus.AVAILABLE),
				new Product("Lenovo G505",12000.0,ProductStatus.AVAILABLE),
				new Product("Lenovo G510",18000.0,ProductStatus.AVAILABLE)
		);
			comfy.setProducts(comfyProducts);
			comfy.start();
		}
		try {
			Thread.sleep(10000);
		} catch(InterruptedException e) {
			LOG.error(e.getMessage(),e);
			Thread.currentThread().interrupt();
		}
		if(Objects.nonNull(rozetka)) {
			List<Product> rozetkaProducts = Arrays.asList(
					new Product("Samsung S8",12455.0,ProductStatus.AVAILABLE),
					new Product("Samsung S9", 22455.0, ProductStatus.AVAILABLE),
					new Product("Samsung S10", 36455.0, ProductStatus.AVAILABLE)
			);

			rozetka.setProducts(rozetkaProducts);
			rozetka.start();
		}
	}
}
