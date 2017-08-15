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
	pay_type varchar(10) NOT NULL,
	merchant_id int REFERENCES pc_merchant
);

CREATE TABLE if not exists pc_invoice(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	invoice_type  varchar(10) NOT NULL,
	total float NOT NULL,
	tax float NOT NULL,
	discount float DEFAULT NULL,
	pay_amount float NOT NULL,
	send_email BOOLEAN NOT NULL,
	send_sms BOOLEAN NOT NULL,
	add_items BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	expiry timestamp NOT NULL,
	merchant_id int REFERENCES pc_merchant,
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

CREATE TABLE if not exists pc_invoice_custom_param (
	id SERIAL PRIMARY KEY NOT NULL,
	param_name varchar(20) NOT NULL,
	param_value varchar(50) DEFAULT NULL,
	provider varchar(20) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);