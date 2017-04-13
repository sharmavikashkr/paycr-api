CREATE TABLE if not exists pc_consumer (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) DEFAULT NULL,
    address_line1 varchar(255) DEFAULT NULL,
    address_line2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL,
    active boolean NOT NULL
);

CREATE TABLE if not exists pc_merchant_setting (
	id SERIAL PRIMARY KEY NOT NULL,
	send_email boolean NOT NULL,
	send_sms boolean NOT NULL,
	expiry_days int NOT NULL,
	rzp_merchant_id varchar(30) DEFAULT NULL,
	rzp_key_id varchar(30) DEFAULT NULL,
	rzp_secret_id varchar(30) DEFAULT NULL
);

CREATE TABLE if not exists pc_merchant (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) DEFAULT NULL,
    access_key varchar(50) DEFAULT NULL,
    secret_key varchar(50) DEFAULT NULL,
    address_line1 varchar(255) DEFAULT NULL,
    address_line2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL,
    setting_id int REFERENCES pc_merchant_setting,
    active boolean NOT NULL
);

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

CREATE TABLE if not exists pc_pricing (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	description varchar(255) NOT NULL,
	rate float NOT NULL,
	invoice_limit int NOT NULL,
    start_amount float NOT NULL,
    end_amount float NOT NULL,
    duration int NOT NULL,
    active boolean NOT NULL
);

CREATE TABLE if not exists pc_merchant_pricing (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
	status varchar(20) NOT NULL,
    merchant_id int REFERENCES pc_merchant,
    pricing_id int REFERENCES pc_pricing
);

CREATE TABLE if not exists pc_payment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	payment_ref_no varchar(50) NOT NULL,
	status varchar(20) NOT NULL,
	method varchar(20) NOT NULL,
	bank varchar(20) DEFAULT NULL,
	wallet varchar(20) DEFAULT NULL
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
	status varchar(20) NOT NULL
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
	param_value varchar(50) NOT NULL,
	provider varchar(20) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_merchant_custom_param (
	id SERIAL PRIMARY KEY NOT NULL,
	param_name varchar(20) NOT NULL,
	provider varchar(20) NOT NULL,
    merchant_setting_id int REFERENCES pc_merchant_setting
);
