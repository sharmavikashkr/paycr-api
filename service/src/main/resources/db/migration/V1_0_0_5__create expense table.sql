CREATE TABLE if not exists pc_supplier (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) DEFAULT NULL,
    gstin varchar(50) DEFAULT NULL,
    active boolean NOT NULL,
    created_by varchar(50) NOT NULL,
	merchant_id int REFERENCES pc_merchant,
	address_id int REFERENCES pc_address
);

CREATE TABLE if not exists pc_expense_payment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	expense_code varchar(20) NOT NULL,
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

CREATE TABLE if not exists pc_expense(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	expense_code varchar(20) NOT NULL,
	total float NOT NULL,
	shipping float DEFAULT NULL,
	discount float DEFAULT NULL,
	pay_amount float NOT NULL,
	add_items BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	merchant_id int REFERENCES pc_merchant,
	supplier_id int REFERENCES pc_supplier,
	payment_id int REFERENCES pc_expense_payment,
	status varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
	updated timestamp DEFAULT NULL,
	updated_by varchar(50) DEFAULT NULL
);


CREATE TABLE if not exists pc_asset (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	code varchar(20) NOT NULL,
	hsnsac varchar(10) DEFAULT NULL,
	description varchar(255) DEFAULT NULL,
	rate float NOT NULL,
	created_by varchar(50) NOT NULL,
	active boolean NOT NULL,
	tax_id int REFERENCES pc_tax_master,
    merchant_id int REFERENCES pc_merchant
);

CREATE TABLE if not exists pc_expense_item (
	id SERIAL PRIMARY KEY NOT NULL,
	quantity int NOT NULL,
	price float NOT NULL,
    expense_id int REFERENCES pc_expense,
	tax_id int REFERENCES pc_tax_master,
    asset_id int REFERENCES pc_asset
);

CREATE TABLE if not exists pc_expense_attachment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	created_by varchar(50) NOT NULL,
    expense_id int REFERENCES pc_expense
);