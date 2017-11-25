CREATE TABLE if not exists pc_consumer (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) DEFAULT NULL,
    email_on_pay BOOLEAN NOT NULL,
    email_on_refund BOOLEAN NOT NULL,
    address_line1 varchar(255) DEFAULT NULL,
    address_line2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL,
    active boolean NOT NULL,
    created_by varchar(50) NOT NULL,
	merchant_id int REFERENCES pc_merchant
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
	tax_name varchar(10) NOT NULL,
	tax_value float NOT NULL,
	discount float DEFAULT NULL,
	pay_amount float NOT NULL,
	add_items BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	expiry timestamp DEFAULT NULL,
	merchant_id int REFERENCES pc_merchant,
	consumer_id int REFERENCES pc_consumer,
	merchant_pricing_id int REFERENCES pc_merchant_pricing,
	payment_id int REFERENCES pc_payment,
	status varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
	expires_in int NOT null,
	never_expire BOOLEAN NOT NULL,
	updated timestamp DEFAULT NULL,
	updated_by varchar(50) DEFAULT NULL,
	parent_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_invoice_notify (
	id SERIAL PRIMARY KEY NOT NULL,
	send_email boolean NOT NULL,
	send_sms boolean NOT NULL,
	email_pdf boolean NOT NULL,
	cc_me boolean NOT NULL,
	cc_email varchar(50) NOT NULL,
	email_note varchar(50) NOT NULL,
	email_subject varchar(50) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_inventory (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	code varchar(20) NOT NULL,
	description varchar(255) DEFAULT NULL,
	rate float NOT NULL,
	created_by varchar(50) NOT NULL,
	active boolean NOT NULL,
    merchant_id int REFERENCES pc_merchant
);

CREATE TABLE if not exists pc_item (
	id SERIAL PRIMARY KEY NOT NULL,
	quantity int NOT NULL,
	price float NOT NULL,
    invoice_id int REFERENCES pc_invoice,
    inventory_id int REFERENCES pc_inventory
);

CREATE TABLE if not exists pc_invoice_custom_param (
	id SERIAL PRIMARY KEY NOT NULL,
	param_name varchar(20) NOT NULL,
	param_value varchar(50) DEFAULT NULL,
	provider varchar(20) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_attachment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	created_by varchar(50) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_bulk_upload (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	file_name varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
    invoice_code varchar(20) NOT NULL
);

CREATE TABLE if not exists pc_bulk_category (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	categories varchar(255) NOT NULL,
	invoice_type  varchar(10) NOT NULL,
	created_by varchar(50) NOT NULL,
	message varchar(255) NOT NULL,
    invoice_code varchar(20) NOT NULL
);