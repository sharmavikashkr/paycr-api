CREATE TABLE if not exists pm_consumer (
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

CREATE TABLE if not exists pm_merchant (
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
    active boolean NOT NULL
);

CREATE TABLE if not exists pm_user (
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

CREATE TABLE if not exists pm_pricing (
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

CREATE TABLE if not exists pm_merchant_pricing (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
	status varchar(20) NOT NULL,
    merchant_id int REFERENCES pm_merchant,
    pricing_id int REFERENCES pm_pricing
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
	merchant int NOT NULL,
	original_amount float NOT NULL,
	pay_amount float NOT NULL,
	shipping float DEFAULT NULL,
	discount float DEFAULT NULL,
	send_email BOOLEAN NOT NULL,
	send_sms BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	expiry timestamp NOT NULL,
	consumer_id int REFERENCES pm_consumer,
	merchant_pricing_id int REFERENCES pm_merchant_pricing,
	payment_id int REFERENCES pm_payment,
	status varchar(20) NOT NULL
);

CREATE TABLE if not exists pm_item (
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(50) NOT NULL,
	quantity int NOT NULL,
	rate float NOT NULL,
	price float NOT NULL,
    invoice_id int REFERENCES pm_invoice
);

CREATE TABLE if not exists pm_reset_password (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	reset_code varchar(50) NOT NULL,
	email varchar(50) NOT NULL,
	status varchar(20) NOT NULL
);

insert into pm_pricing (created,name,description,rate,invoice_limit,start_amount,end_amount,duration,active) values(now(),'FREE TRIAL','2 Momths free trial',0.00,100,1.00,10000.00,90,true);
