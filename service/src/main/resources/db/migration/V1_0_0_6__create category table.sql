CREATE TABLE if not exists pc_consumer_category (
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(50) NOT NULL,
	value varchar(50) NOT NULL,
    consumer_id int REFERENCES pc_consumer
);

CREATE TABLE if not exists pc_bulk_consumer_upload (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	file_name varchar(20) NOT NULL,
	created_by varchar(50) NOT NULL,
    merchant_id int REFERENCES pc_merchant
);