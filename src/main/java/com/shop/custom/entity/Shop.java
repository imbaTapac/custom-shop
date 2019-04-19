package com.shop.custom.entity;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.shop.custom.utils.DAOUtils;

public abstract class Shop extends Thread {
	private static final Logger LOG = LoggerFactory.getLogger(Shop.class);

	private Long id;
	private String name;
	private List<Category> categories = new ArrayList<>();
	private List<Product> products = new ArrayList<>();

	public Shop() {
	}

	public Shop(Long id, String name, List<Category> categories) {
		this.id = id;
		this.name = name;
		this.categories = categories;
	}

	public Long getShopId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShopName() {
		return name;
	}

	public void setShopName(String name) {
		this.name = name;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public List<Product> getProducts() {
		return products;
	}

	@Override
	public void run() {
		Connection connection;
		LOG.debug("My thread is {}", currentThread().getName());
		LOG.debug("My className is {}", currentThread().getClass().getName());
		try {
			LOG.debug("Trying to get connection");
			connection = DAOUtils.getConnection();
			if(nonNull(connection)) {
				LOG.debug("Successfully connected to DB");
				LOG.debug("{}", this);
				addNewCategory();
				sleep(10000);
				changeCategoriesStatus();
				sleep(10000);
				LOG.debug("Changing available products price");
				changeProductsPrice();
				LOG.debug("{}", this);
				DAOUtils.close(connection);
			}
		} catch(InterruptedException e) {
			LOG.debug(e.getMessage(), e);
			currentThread().interrupt();
		} catch(SQLException e) {
			LOG.debug(e.getMessage(), e);
		} finally {
			LOG.debug("{} finished his work", currentThread().getName());
		}
	}

	public List<Category> getShopCategories() {
		try {
			Connection connection = DAOUtils.getConnection();
			ResultSet resultSet;
			try(PreparedStatement statement = (PreparedStatement) connection.prepareStatement("SELECT * FROM category WHERE shop_id = ? ")) {
				statement.setLong(1, id);
				resultSet = statement.executeQuery();
				return DAOUtils.mapCategories(resultSet);
			}
		} catch(SQLException e) {
			LOG.debug(e.getMessage(), e);
		}
		return Collections.emptyList();
	}

	public List<Product> getCategoryProducts(Category category) {
		try {
			Connection connection = DAOUtils.getConnection();
			ResultSet resultSet;
			try(PreparedStatement statement = (PreparedStatement) connection.prepareStatement("SELECT * FROM product WHERE category_id = ? ")) {
				statement.setLong(1, category.getId());
				resultSet = statement.executeQuery();
				return DAOUtils.mapProducts(resultSet);
			}
		} catch(SQLException e) {
			LOG.debug(e.getMessage(), e);
		}
		return Collections.emptyList();
	}

	private Product saveProduct(Product product, Long categoryId) {
		try {
			Connection connection = DAOUtils.getConnection();
			try(PreparedStatement statement = (PreparedStatement) connection.prepareStatement("INSERT INTO product(title,price,status,category_id) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, product.getTitle());
				statement.setDouble(2, product.getPrice());
				statement.setString(3, product.getStatus());
				statement.setLong(4, categoryId);
				statement.executeUpdate();
				long lastInsertId = statement.getLastInsertID();
				return selectProduct(connection, product, lastInsertId);
			}
		} catch(SQLException e) {
			LOG.debug(e.getMessage(), e);
		}
		return product;
	}

	private Product changeStatus(Product product, ProductStatus status) {
		try {
			Connection connection = DAOUtils.getConnection();
			try(PreparedStatement statement = (PreparedStatement) connection.prepareStatement("UPDATE product SET status = ? WHERE id = ? ")) {
				statement.setString(1, status.getStatus());
				statement.setLong(2, product.getId());
				statement.executeUpdate();
				return selectProduct(connection, product, null);
			}
		} catch(SQLException e) {
			LOG.debug(e.getMessage(), e);
		}
		return product;
	}

	private Product changePrice(Product product, Double on) {
		try {
			Connection connection = DAOUtils.getConnection();
			double finalPrice = product.getPrice() + product.getPrice() * on;
			try(PreparedStatement statement = (PreparedStatement) connection.prepareStatement("UPDATE product SET price = ? WHERE id = ? ")) {
				statement.setDouble(1, finalPrice);
				statement.setLong(2, product.getId());
				statement.executeUpdate();
				return selectProduct(connection, product, null);
			}
		} catch(SQLException e) {
			LOG.debug(e.getMessage(), e);
		}
		return product;
	}

	private Product selectProduct(Connection connection, Product product, Long lastInsertId) {
		try(PreparedStatement statement = (PreparedStatement) connection.prepareStatement("SELECT * FROM product WHERE id = ?")) {
			long productId = isNull(lastInsertId) ? product.getId() : lastInsertId;
			statement.setLong(1, productId);
			ResultSet resultSet = statement.executeQuery();
			return DAOUtils.mapProduct(product, resultSet);
		} catch(SQLException e) {

			LOG.debug(e.getMessage(), e);
		}
		return product;
	}

	private void addNewCategory() {
		Category toUpdate = getCategories().stream()
				.filter(category -> category.getId() % 3 == 0)
				.findFirst().orElse(null);
		int categoryIndex = getCategories().indexOf(toUpdate);
		LOG.debug("Old category {}", toUpdate);
		getProducts().forEach(
				product -> {
					if(toUpdate != null) {
						toUpdate.getProducts().add(saveProduct(product, toUpdate.getId()));
					}
				}
		);
		getCategories().set(categoryIndex, toUpdate);
		LOG.debug("New category {}", toUpdate);
		LOG.debug("{}", this);
	}

	private void changeCategoriesStatus() {
		int index = ThreadLocalRandom.current().nextInt(0, getCategories().size());
		LOG.debug("Changing status of category {}", getCategories().get(index).getName());
		List<Product> toAbsent = getCategories().get(index).getProducts();
		toAbsent.forEach(product -> product = changeStatus(product, ProductStatus.ABSENT));
		getCategories().get(index).setProducts(toAbsent);

		List<Category> toExpected = getCategories().stream()
				.filter(category -> !Objects.equals(category, getCategories().get(index))).collect(Collectors.toList());

		toExpected.forEach(category -> category.getProducts().forEach(product ->
		{
			if(product.getId() % 2 == 0) {
				changeStatus(product, ProductStatus.EXPECTED);
			}
		}));

		for(Category category : toExpected) {
			int indexOf = getCategories().indexOf(category);
			getCategories().set(indexOf, category);
		}
		LOG.debug("{}", this);
	}

	private void changeProductsPrice() {
		List<Product> productToUpdate = getCategories().stream()
				.flatMap(category -> category.getProducts().stream()
						.filter(product -> Objects.equals(product.getStatus(), ProductStatus.AVAILABLE.getStatus())))
				.collect(Collectors.toList());

		productToUpdate.forEach(product -> changePrice(product, 0.2));
		for(Category category : getCategories()) {
			for(Product product : category.getProducts()) {
				int indexOf = category.getProducts().indexOf(product);
				category.getProducts().set(indexOf, product);
			}
		}
	}

	@Override
	public String toString() {
		return "Shop{" +
				"id=" + id +
				", name='" + name + '\'' +
				", categories=" + categories +
				'}';
	}

}
