CREATE TABLE if not exists pm_consumer (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) DEFAULT NULL,
    address_line_1 varchar(255) DEFAULT NULL,
    address_line_2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL
);

CREATE TABLE if not exists pm_merchant (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) DEFAULT NULL,
    access_key varchar(50) DEFAULT NULL,
    secret_key varchar(50) DEFAULT NULL,
    address_line_1 varchar(255) DEFAULT NULL,
    address_line_2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL
);

CREATE TABLE if not exists pm_user (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) NOT NULL,
    password varchar(100) NOT NULL,
    address_line_1 varchar(255) DEFAULT NULL,
    address_line_2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL
);

CREATE TABLE if not exists pm_user_role(
	id SERIAL PRIMARY KEY NOT NULL,
	pm_user int REFERENCES pm_user,
	role varchar(50) NOT NULL
);

CREATE TABLE if not exists pm_merchatnt_user(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int NOT NULL,
	user_id int NOT NULL
);

CREATE TABLE if not exists pm_payment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	payment_ref_no varchar(50) NOT NULL,
	pay_mode varchar(15) NOT NULL,
	status varchar(20) NOT NULL
);

CREATE TABLE if not exists pm_invoice(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	bill_no varchar(10) DEFAULT NULL,
	merchant int NOT NULL,
	amount float NOT NULL,
	shipping float DEFAULT NULL,
	discount float DEFAULT NULL,
	send_email BOOLEAN NOT NULL,
	send_sms BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	expiry timestamp NOT NULL,
	consumer int REFERENCES pm_consumer,
	payment int REFERENCES pm_payment,
	status varchar(20) NOT NULL
);

CREATE TABLE if not exists pm_item (
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(50) NOT NULL,
	quantity int NOT NULL,
	rate float NOT NULL,
	price float NOT NULL,
    invoice integer REFERENCES pm_invoice
);
