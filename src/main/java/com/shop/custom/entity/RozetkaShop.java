package com.shop.custom.entity;

import static java.util.Objects.isNull;

import com.shop.custom.utils.DAOUtils;

public class RozetkaShop extends Shop {
	private static volatile RozetkaShop rozetkaShop = null;

	public static RozetkaShop getInstance() {
		RozetkaShop localInstance = rozetkaShop;
		if(isNull(localInstance)) {
			synchronized(RozetkaShop.class) {
				localInstance = rozetkaShop;
				if(isNull(localInstance)) {
					rozetkaShop = (RozetkaShop) DAOUtils.getShop("Rozetka");
				}
			}
		}
		return rozetkaShop;
	}
}
