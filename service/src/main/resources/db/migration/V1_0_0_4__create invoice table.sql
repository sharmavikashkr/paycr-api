CREATE TABLE if not exists pc_consumer (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    mobile varchar(15) NOT NULL,
    gstin varchar(50) DEFAULT NULL,
    email_on_pay BOOLEAN NOT NULL,
    email_on_refund BOOLEAN NOT NULL,
    type varchar(15) DEFAULT NULL,
    active boolean NOT NULL,
    created_by varchar(50) NOT NULL,
	merchant_id int REFERENCES pc_merchant,
	billing_address_id int REFERENCES pc_address,
	shipping_address_id int REFERENCES pc_address
);

CREATE TABLE if not exists pc_consumer_category (
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(50) NOT NULL,
	value varchar(50) NOT NULL,
    consumer_id int REFERENCES pc_consumer
);

CREATE TABLE if not exists pc_invoice_payment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	payment_ref_no varchar(50) NOT NULL,
	amount float NOT NULL,
	paid_date timestamp NOT NULL,
	status varchar(20) NOT NULL,
	method varchar(20) NOT NULL,
	pay_mode varchar(20) NOT NULL,
	pay_type varchar(10) NOT NULL,
	merchant_id int REFERENCES pc_merchant
);

CREATE TABLE if not exists pc_invoice_credit_note (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	note_code varchar(20) NOT NULL,
	total float NOT NULL,
	adjustment float NOT NULL,
	pay_amount float NOT NULL,
	currency varchar(10) NOT NULL,
	merchant_id int REFERENCES pc_merchant,
	consumer_id int REFERENCES pc_consumer,
	payment_id int REFERENCES pc_invoice_payment,
	created_by varchar(50) NOT NULL
);

CREATE TABLE if not exists pc_invoice (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	invoice_code varchar(20) NOT NULL,
	invoice_type  varchar(10) NOT NULL,
	invoice_date timestamp NOT NULL,
	total float NOT NULL,
	shipping float DEFAULT NULL,
	discount float DEFAULT NULL,
	pay_amount float NOT NULL,
	add_items BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	expiry timestamp DEFAULT NULL,
	merchant_id int REFERENCES pc_merchant,
	consumer_id int REFERENCES pc_consumer,
	merchant_pricing_id int REFERENCES pc_merchant_pricing,
	payment_id int REFERENCES pc_invoice_payment,
	credit_note_id int REFERENCES pc_invoice_credit_note,
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
	hsnsac varchar(10) DEFAULT NULL,
	description varchar(255) DEFAULT NULL,
	rate float NOT NULL,
	created_by varchar(50) NOT NULL,
	type varchar(15) DEFAULT NULL,
	active boolean NOT NULL,
	tax_id int REFERENCES pc_tax_master,
    merchant_id int REFERENCES pc_merchant
);

CREATE TABLE if not exists pc_invoice_item (
	id SERIAL PRIMARY KEY NOT NULL,
	quantity int NOT NULL,
	price float NOT NULL,
    invoice_id int REFERENCES pc_invoice,
    invoice_credit_note_id int REFERENCES pc_invoice_credit_note,
	tax_id int REFERENCES pc_tax_master,
    inventory_id int REFERENCES pc_inventory
);

CREATE TABLE if not exists pc_invoice_custom_param (
	id SERIAL PRIMARY KEY NOT NULL,
	param_name varchar(20) NOT NULL,
	param_value varchar(50) DEFAULT NULL,
	provider varchar(20) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_invoice_attachment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	created_by varchar(50) NOT NULL,
    invoice_id int REFERENCES pc_invoice
);