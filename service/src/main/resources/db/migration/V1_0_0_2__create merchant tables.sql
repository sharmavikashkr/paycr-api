CREATE TABLE if not exists pc_invoice_setting (
	id SERIAL PRIMARY KEY NOT NULL,
	send_email boolean NOT NULL,
	send_sms boolean NOT NULL,
	add_items boolean NOT NULL,
	email_pdf boolean NOT NULL,
	cc_me boolean NOT NULL,
	expiry_days int NOT NULL,
	email_note varchar(50) DEFAULT NULL,
	email_subject varchar(50) DEFAULT NULL
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
	banner varchar(20) DEFAULT NULL,
    active boolean NOT NULL,
    payment_setting_id int REFERENCES pc_payment_setting,
	invoice_setting_id int REFERENCES pc_invoice_setting,
	address_id int REFERENCES pc_address
);

CREATE TABLE if not exists pc_pricing (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	code varchar(20) NOT NULL,
	name varchar(50) NOT NULL,
	description varchar(255) NOT NULL,
	rate float NOT NULL,
	use_limit int NOT NULL,
    type varchar(20) NOT NULL,
	hsnsac varchar(10) DEFAULT NULL,
    duration int NOT NULL,
    active boolean NOT NULL,
	tax_id int REFERENCES pc_tax_master
);

CREATE TABLE if not exists pc_pricing_merchant (
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int REFERENCES pc_merchant,
	pricing_id int REFERENCES pc_pricing
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
	use_count int NOT NULL,
    merchant_id int REFERENCES pc_merchant,
    pricing_id int REFERENCES pc_pricing,
    subscription_id int REFERENCES pc_subscription
);

CREATE TABLE if not exists pc_report(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	description varchar(255) DEFAULT NULL,
	time_range varchar(20) NOT NULL,
	report_type varchar(20) NOT NULL,
	pay_type varchar(20) DEFAULT NULL,
	pay_mode varchar(20) DEFAULT NULL,
	merchant_id int REFERENCES pc_merchant
);