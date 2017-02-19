CREATE TABLE if not exists pm_consumer (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name char(50) DEFAULT NULL,
    email char(50) DEFAULT NULL,
    mobile char(15) DEFAULT NULL,
    address_line_1 char(100) DEFAULT NULL,
    address_line_2 char(100) DEFAULT NULL,
    city char(30) DEFAULT NULL,
    district char(30) DEFAULT NULL,
    state char(50) DEFAULT NULL,
    country char(50) DEFAULT NULL,
    pincode char(10) DEFAULT NULL
);

CREATE TABLE if not exists pm_merchant (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name char(50) DEFAULT NULL,
    email char(50) DEFAULT NULL,
    mobile char(15) DEFAULT NULL,
    access_key char(50) DEFAULT NULL,
    secret_key char(50) DEFAULT NULL,
    address_line_1 char(100) DEFAULT NULL,
    address_line_2 char(100) DEFAULT NULL,
    city char(30) DEFAULT NULL,
    district char(30) DEFAULT NULL,
    state char(50) DEFAULT NULL,
    country char(50) DEFAULT NULL,
    pincode char(10) DEFAULT NULL
);

CREATE TABLE if not exists pm_user (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name char(50) DEFAULT NULL,
    email char(50) DEFAULT NULL,
    mobile char(15) NOT NULL,
    password char(50) NOT NULL,
    roles char(100) NOT NULL,
    address_line_1 char(100) DEFAULT NULL,
    address_line_2 char(100) DEFAULT NULL,
    city char(30) DEFAULT NULL,
    district char(30) DEFAULT NULL,
    state char(50) DEFAULT NULL,
    country char(50) DEFAULT NULL,
    pincode char(10) DEFAULT NULL
);

CREATE TABLE if not exists pm_merchatnt_user(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int NOT NULL,
	user_id int NOT NULL
);

CREATE TABLE if not exists pm_invoice(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code int NOT NULL,
	merchant int NOT NULL,
	amount float NOT NULL,
	currency char(10) NOT NULL,
	expiry timestamp NOT NULL,
	consumer int REFERENCES pm_consumer
);

CREATE TABLE if not exists pm_item (
	id SERIAL PRIMARY KEY NOT NULL,
	name char(50) NOT NULL,
	quantity int NOT NULL,
	rate float NOT NULL,
	price float NOT NULL,
    invoice integer REFERENCES pm_invoice
);

CREATE TABLE if not exists pm_payment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	payment_ref_no char(50) NOT NULL,
	pay_mode char(15) NOT NULL,
	status char(20) NOT NULL,
	invoice int REFERENCES pm_invoice
);
