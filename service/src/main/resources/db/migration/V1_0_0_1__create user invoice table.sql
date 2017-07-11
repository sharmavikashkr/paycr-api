CREATE TABLE if not exists pc_user (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) NOT NULL,
    password varchar(100) NOT NULL,
    address_line1 varchar(255) DEFAULT NULL,
    address_line2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL,
    active boolean NOT NULL
);

CREATE TABLE if not exists pc_user_role(
	id SERIAL PRIMARY KEY NOT NULL,
	pc_user int REFERENCES pc_user,
	role varchar(50) NOT NULL
);

CREATE TABLE if not exists pc_merchant_user(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int NOT NULL,
	user_id int NOT NULL
);

CREATE TABLE if not exists pc_payment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	payment_ref_no varchar(50) NOT NULL,
	amount float NOT NULL,
	status varchar(20) NOT NULL,
	method varchar(20) NOT NULL,
	bank varchar(20) DEFAULT NULL,
	wallet varchar(20) DEFAULT NULL,
	pay_mode varchar(20) NOT NULL,
	pay_type varchar(10) NOT NULL
);

CREATE TABLE if not exists pc_invoice(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	merchant int NOT NULL,
	original_amount float NOT NULL,
	pay_amount float NOT NULL,
	shipping float DEFAULT NULL,
	discount float DEFAULT NULL,
	send_email BOOLEAN NOT NULL,
	send_sms BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	expiry timestamp NOT NULL,
	consumer_id int REFERENCES pc_consumer,
	merchant_pricing_id int REFERENCES pc_merchant_pricing,
	payment_id int REFERENCES pc_payment,
	status varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL
);

CREATE TABLE if not exists pc_item (
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(50) NOT NULL,
	quantity int NOT NULL,
	rate float NOT NULL,
	price float NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_reset_password (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	reset_code varchar(50) NOT NULL,
	email varchar(50) NOT NULL,
	status varchar(20) NOT NULL
);

CREATE TABLE if not exists pc_invoice_custom_param (
	id SERIAL PRIMARY KEY NOT NULL,
	param_name varchar(20) NOT NULL,
	param_value varchar(50) DEFAULT NULL,
	provider varchar(20) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_merchant_custom_param (
	id SERIAL PRIMARY KEY NOT NULL,
	param_name varchar(20) NOT NULL,
	provider varchar(20) NOT NULL,
    invoice_setting_id int REFERENCES pc_invoice_setting
);

CREATE TABLE if not exists pc_notification(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int DEFAULT NULL,
	user_id int DEFAULT NULL,
	subject varchar(50) NOT NULL,
	message varchar(255) NOT NULL,
	created timestamp NOT NULL,
	read boolean NOT NULL
);