CREATE TABLE if not exists pc_promotion (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(100) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    phone varchar(20) NOT NULL,
    created_by varchar(50) NOT NULL,
    sent boolean NOT NULL,
    notified int NOT NULL,
    address_id int REFERENCES pc_address
);