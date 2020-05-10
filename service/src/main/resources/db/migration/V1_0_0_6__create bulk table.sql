CREATE TABLE if not exists pc_bulk_invoice_upload (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	file_name varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
    invoice_code varchar(20) NOT NULL
);

CREATE TABLE if not exists pc_bulk_flag (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	flags varchar(255) NOT NULL,
	invoice_type  varchar(10) NOT NULL,
	created_by varchar(50) NOT NULL,
	message varchar(255) NOT NULL,
    invoice_code varchar(20) NOT NULL
);

CREATE TABLE if not exists pc_bulk_consumer_upload (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	file_name varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
    merchant_id int REFERENCES pc_merchant NOT NULL
);

CREATE TABLE if not exists pc_bulk_inventory_upload (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	file_name varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
    merchant_id int REFERENCES pc_merchant NOT NULL
);

CREATE TABLE if not exists pc_bulk_supplier_upload (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	file_name varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
    merchant_id int REFERENCES pc_merchant NOT NULL
);

CREATE TABLE if not exists pc_bulk_asset_upload (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	file_name varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
    merchant_id int REFERENCES pc_merchant NOT NULL
);