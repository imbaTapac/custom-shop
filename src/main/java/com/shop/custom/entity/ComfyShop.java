package com.shop.custom.entity;

import static java.util.Objects.isNull;

import com.shop.custom.utils.DAOUtils;
import com.shop.custom.factory.ShopFactory;

public class ComfyShop extends Shop {
	private static volatile ComfyShop comfyShop = null;

	public static ComfyShop getInstance() {
		ComfyShop localInstance = comfyShop;
		if(isNull(localInstance)) {
			synchronized(ShopFactory.class) {
				localInstance = comfyShop;
				if(isNull(localInstance)) {
					comfyShop = (ComfyShop) DAOUtils.getShop("Comfy");
				}
			}
		}
		return comfyShop;
	}
}
