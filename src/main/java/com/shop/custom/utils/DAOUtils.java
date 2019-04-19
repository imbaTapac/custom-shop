package com.shop.custom.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.shop.custom.entity.Category;
import com.shop.custom.entity.ComfyShop;
import com.shop.custom.entity.Product;
import com.shop.custom.entity.RozetkaShop;
import com.shop.custom.entity.Shop;


public class DAOUtils {

	private DAOUtils(){
	}

	private static final Logger LOG = LoggerFactory.getLogger(DAOUtils.class);

	private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/";
	private static String userName;
	private static String password;
	private static String dbName;
	private static final ThreadLocal<Connection> connection = new ThreadLocal<>();

	public static Connection getConnection() throws SQLException {
		try {
			if(connection.get() != null) {
				return connection.get();
			}
			connection.set((Connection) DriverManager.getConnection(CONNECTION_URL + dbName, userName, password));
			return connection.get();
		} catch(SQLException e) {
			LOG.error(e.getMessage(), e);
		}
		throw new SQLException("Cannot connect to DB");
	}

	public static Shop getShop(String shopName) {
		Connection connection;
		ResultSet resultSet;
		Shop shop = null;
		try {
			connection = getConnection();
			try(PreparedStatement statement = (PreparedStatement) connection.prepareStatement("SELECT * FROM shop WHERE name = ?;")) {
				statement.setString(1, shopName);
				resultSet = statement.executeQuery();
				return mapShop(shop, resultSet);
			}
		} catch(SQLException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private static Shop mapShop(Shop shop, ResultSet resultSet) throws SQLException {
		while(resultSet.next()) {
			String name = resultSet.getString("name");
			if(name.equalsIgnoreCase("Comfy")) {
				shop = new ComfyShop();
			} else if(name.equalsIgnoreCase("Rozetka")) {
				shop = new RozetkaShop();
			} else {
				return null;
			}
			shop.setId(resultSet.getLong("id"));
			shop.setShopName(name);
		}
		shop.setCategories(shop.getShopCategories());
		Shop finalShop = shop;
		shop.getCategories().forEach(category ->
				category.setProducts(finalShop.getCategoryProducts(category)));
		resultSet.close();
		return finalShop;
	}

	public static Category mapCategory(Category category, ResultSet resultSet) throws SQLException {
		while(resultSet.next()) {
			category.setId(resultSet.getLong("id"));
			category.setName(resultSet.getString("name"));
		}
		resultSet.close();
		return category;
	}

	public static List<Category> mapCategories(ResultSet resultSet) throws SQLException {
		List<Category> categories = new ArrayList<>();
		while(resultSet.next()) {
			Category category = new Category();
			category.setId(resultSet.getLong("id"));
			category.setName(resultSet.getString("name"));
			categories.add(category);
		}
		resultSet.close();
		return categories;
	}

	public static Product mapProduct(Product product, ResultSet resultSet) throws SQLException {
		while(resultSet.next()) {
			product.setId(resultSet.getLong("id"));
			product.setPrice(resultSet.getDouble("price"));
			product.setStatus(resultSet.getString("status"));
			product.setTitle(resultSet.getString("title"));
		}
		resultSet.close();
		return product;
	}

	public static List<Product> mapProducts(ResultSet resultSet) throws SQLException {
		List<Product> products = new ArrayList<>();
		while(resultSet.next()) {
			Product product = new Product();
			product.setId(resultSet.getLong("id"));
			product.setPrice(resultSet.getDouble("price"));
			product.setStatus(resultSet.getString("status"));
			product.setTitle(resultSet.getString("title"));
			products.add(product);
		}
		resultSet.close();
		return products;
	}

	public static void close(Connection connect) {
		try {
			LOG.debug("Trying to close connection");
			if(connect != null) {
				connect.close();
				connection.remove();
				LOG.debug("Connection was successfully closed");
			}
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	static {
		LOG.debug("Loading static context");
		Properties properties = new Properties();
		InputStream in = DAOUtils.class.getClassLoader().getResourceAsStream("db.properties");
		try {
			properties.load(in);
			userName = properties.getProperty("db.username");
			password = properties.getProperty("db.password");
			dbName = properties.getProperty("db.name");
			in.close();
		} catch(IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
