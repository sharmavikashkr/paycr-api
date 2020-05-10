CREATE TABLE if not exists pc_supplier (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    mobile varchar(15) NOT NULL,
    gstin varchar(50) DEFAULT NULL,
    active boolean NOT NULL,
    created_by varchar(50) NOT NULL,
	merchant_id int REFERENCES pc_merchant NOT NULL,
	address_id int REFERENCES pc_address
);

CREATE TABLE if not exists pc_expense_payment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	expense_code varchar(20) NOT NULL,
	payment_ref_no varchar(50) NOT NULL,
	amount float NOT NULL,
	paid_date timestamp NOT NULL,
	status varchar(20) NOT NULL,
	method varchar(20) NOT NULL,
	pay_mode varchar(20) NOT NULL,
	pay_type varchar(10) NOT NULL,
	deleted BOOLEAN NOT NULL,
	merchant_id int REFERENCES pc_merchant NOT NULL
);

CREATE TABLE if not exists pc_expense_note (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	expense_code varchar(20) NOT NULL,
	note_code varchar(20) NOT NULL,
	note_type varchar(20) NOT NULL,
	note_date timestamp NOT NULL,
	note_reason varchar(255) NOT NULL,
	total float NOT NULL,
	total_price float NOT NULL,
	adjustment float NOT NULL,
	pay_amount float NOT NULL,
	currency varchar(10) NOT NULL,
	deleted BOOLEAN NOT NULL,
	merchant_id int REFERENCES pc_merchant NOT NULL,
	supplier_id int REFERENCES pc_supplier NOT NULL,
	created_by varchar(50) NOT NULL
);

CREATE TABLE if not exists pc_expense(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	expense_code varchar(20) NOT NULL,
	invoice_code varchar(30) NOT NULL,
	invoice_date timestamp NOT NULL,
	total float NOT NULL,
	total_price float NOT NULL,
	shipping float NOT NULL,
	discount float NOT NULL,
	pay_amount float NOT NULL,
	add_items BOOLEAN NOT NULL,
	currency varchar(10) NOT NULL,
	merchant_id int REFERENCES pc_merchant NOT NULL,
	supplier_id int REFERENCES pc_supplier NOT NULL,
	merchant_pricing_id int REFERENCES pc_merchant_pricing NOT NULL,
	payment_id int REFERENCES pc_expense_payment,
	status varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
	updated timestamp DEFAULT NULL,
	updated_by varchar(50) DEFAULT NULL,
	deleted BOOLEAN NOT NULL
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
	type varchar(15) NOT NULL,
	active boolean NOT NULL,
	tax_id int REFERENCES pc_tax_master NOT NULL,
    merchant_id int REFERENCES pc_merchant NOT NULL
);

CREATE TABLE if not exists pc_expense_item (
	id SERIAL PRIMARY KEY NOT NULL,
	quantity int NOT NULL,
	price float NOT NULL,
    expense_id int REFERENCES pc_expense,
    expense_note_id int REFERENCES pc_expense_note,
	tax_id int REFERENCES pc_tax_master NOT NULL,
    asset_id int REFERENCES pc_asset NOT NULL
);

CREATE TABLE if not exists pc_expense_attachment (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	created_by varchar(50) NOT NULL,
    expense_id int REFERENCES pc_expense NOT NULL
);