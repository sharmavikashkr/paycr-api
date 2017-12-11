CREATE TABLE if not exists pc_address (
	id SERIAL PRIMARY KEY NOT NULL,
    address_line1 varchar(255) DEFAULT NULL,
    address_line2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL
);

CREATE TABLE if not exists pc_tax_master (
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(20) NOT NULL,
	value float NOT NULL,
	active boolean NOT NULL,
	child boolean NOT NULL,
	parent_id int REFERENCES pc_tax_master
);

CREATE TABLE if not exists pc_payment_setting (
	id SERIAL PRIMARY KEY NOT NULL,
	rzp_merchant_id varchar(30) DEFAULT NULL,
	rzp_key_id varchar(30) DEFAULT NULL,
	rzp_secret_id varchar(30) DEFAULT NULL
);

CREATE TABLE if not exists pc_admin_setting (
	id SERIAL PRIMARY KEY NOT NULL,
	gstin varchar(50) DEFAULT NULL,
	hsnsac varchar(10) DEFAULT NULL,
	banner varchar(20) DEFAULT NULL,
	tax_id int REFERENCES pc_tax_master,
	address_id int REFERENCES pc_address,
	payment_setting_id int REFERENCES pc_payment_setting
);

CREATE TABLE if not exists pc_invoice_setting (
	id SERIAL PRIMARY KEY NOT NULL,
	send_email boolean NOT NULL,
	send_sms boolean NOT NULL,
	add_items boolean NOT NULL,
	email_pdf boolean NOT NULL,
	cc_me boolean NOT NULL,
	expiry_days int NOT NULL,
	email_note varchar(50) DEFAULT NULL,
	email_subject varchar(50) DEFAULT NULL,
	banner varchar(20) DEFAULT NULL
);

CREATE TABLE if not exists pc_merchant_custom_param (
	id SERIAL PRIMARY KEY NOT NULL,
	param_name varchar(20) NOT NULL,
	provider varchar(20) NOT NULL,
    invoice_setting_id int REFERENCES pc_invoice_setting
);

CREATE TABLE if not exists pc_merchant (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    mobile varchar(15) NOT NULL,
    access_key varchar(50) NOT NULL,
    secret_key varchar(50) NOT NULL,
    gstin varchar(50) DEFAULT NULL,
    active boolean NOT NULL,
    payment_setting_id int REFERENCES pc_payment_setting,
	invoice_setting_id int REFERENCES pc_invoice_setting,
	address_id int REFERENCES pc_address
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

CREATE TABLE if not exists pc_subscription(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	subscription_code varchar(20) NOT NULL,
	total float NOT NULL,
	pay_amount float NOT NULL,
	quantity int NOT NULL,
	currency varchar(10) NOT NULL,
    pay_mode varchar(20) NOT NULL,
	payment_ref_no varchar(50) DEFAULT NULL,
	status varchar(20) NOT NULL,
	method varchar(20) DEFAULT NULL,
	bank varchar(20) DEFAULT NULL,
	wallet varchar(20) DEFAULT NULL,
	merchant_id int REFERENCES pc_merchant,
	tax_id int REFERENCES pc_tax_master,
    pricing_id int REFERENCES pc_pricing
);

CREATE TABLE if not exists pc_merchant_pricing (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
	status varchar(20) NOT NULL,
	quantity int NOT NULL,
	inv_count int NOT NULL,
    merchant_id int REFERENCES pc_merchant,
    pricing_id int REFERENCES pc_pricing,
    subscription_id int REFERENCES pc_subscription
);