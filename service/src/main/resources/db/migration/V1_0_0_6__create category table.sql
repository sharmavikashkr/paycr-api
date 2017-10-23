CREATE TABLE if not exists pc_consumer_category (
	id SERIAL PRIMARY KEY NOT NULL,
	name varchar(50) NOT NULL,
	value varchar(50) NOT NULL,
    consumer_id int REFERENCES pc_consumer
);