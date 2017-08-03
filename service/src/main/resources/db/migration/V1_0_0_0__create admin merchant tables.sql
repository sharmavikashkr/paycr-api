CREATE TABLE if not exists pc_payment_setting (
	id SERIAL PRIMARY KEY NOT NULL,
	rzp_merchant_id varchar(30) DEFAULT NULL,
	rzp_key_id varchar(30) DEFAULT NULL,
	rzp_secret_id varchar(30) DEFAULT NULL
);

CREATE TABLE if not exists pc_invoice_setting (
	id SERIAL PRIMARY KEY NOT NULL,
	send_email boolean NOT NULL,
	send_sms boolean NOT NULL,
	add_items boolean NOT NULL,
	expiry_days int NOT NULL,
	tax float NOT NULL
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
    payment_setting_id int REFERENCES pc_payment_setting,
	invoice_setting_id int REFERENCES pc_invoice_setting,
    active boolean NOT NULL
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

CREATE TABLE if not exists pc_subscription_mode(
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(50) DEFAULT NULL,
	pay_mode varchar(20) DEFAULT NULL,
	rzp_merchant_id varchar(30) DEFAULT NULL,
	rzp_key_id varchar(30) DEFAULT NULL,
	rzp_secret_id varchar(30) DEFAULT NULL,
	active boolean NOT NULL
);

CREATE TABLE if not exists pc_subscription(
	id SERIAL PRIMARY KEY NOT NULL,
	subscription_code varchar(20) NOT NULL,
	amount float NOT NULL,
	quantity int NOT NULL,
	currency varchar(10) NOT NULL,
	created timestamp NOT NULL,
	payment_ref_no varchar(50) DEFAULT NULL,
	status varchar(20) NOT NULL,
	method varchar(20) DEFAULT NULL,
	bank varchar(20) DEFAULT NULL,
	wallet varchar(20) DEFAULT NULL,
	merchant_id int REFERENCES pc_merchant,
    pricing_id int REFERENCES pc_pricing,
    subscription_mode_id int REFERENCES pc_subscription_mode
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