CREATE DATABASE IF NOT EXISTS custom_shop;

USE custom_shop;

CREATE TABLE `Shop` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`name` nvarchar(128) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `Category` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`name` nvarchar(128) NOT NULL,
	`shop_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
    FOREIGN KEY fk_shop(`shop_id`) REFERENCES `Shop`(`id`)
);

CREATE TABLE `Product` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`title` nvarchar(56) NOT NULL,
	`price` FLOAT NOT NULL,
	`status` nvarchar(9) NOT NULL,
	`category_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
    FOREIGN KEY fk_cat(`category_id`) REFERENCES `Category`(`id`)
);


