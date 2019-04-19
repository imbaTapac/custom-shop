package com.shop.custom.factory;

import com.shop.custom.entity.ComfyShop;
import com.shop.custom.entity.RozetkaShop;
import com.shop.custom.entity.Shop;

public class ShopFactory {
	private ShopFactory() {
		throw new IllegalStateException("Fabric class");
	}

	public static Shop getShop(String shop) {
		if(shop.equalsIgnoreCase("Comfy")) {
			return ComfyShop.getInstance();
		} else if(shop.equalsIgnoreCase("Rozetka")) {
			return RozetkaShop.getInstance();
		}
		return null;
	}
}
