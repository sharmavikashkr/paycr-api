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

CREATE TABLE if not exists pc_notification(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int DEFAULT NULL,
	user_id int DEFAULT NULL,
	subject varchar(50) NOT NULL,
	message varchar(255) NOT NULL,
	created timestamp NOT NULL,
	read boolean NOT NULL
);

CREATE TABLE if not exists pc_timeline(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	object_id int NOT NULL,
	object_type varchar(20) NOT NULL,
	internal boolean NOT NULL,
	message varchar(255) NOT NULL,
	created_by varchar(50) NOT NULL
);