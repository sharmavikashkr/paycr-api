CREATE TABLE if not exists pc_user (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) NOT NULL,
    created_by varchar(50) NOT NULL,
    user_type varchar(20) NOT NULL,
    password varchar(100) NOT NULL,
    active boolean NOT NULL,
    address_id int REFERENCES pc_address
);

CREATE TABLE if not exists pc_user_role(
	id SERIAL PRIMARY KEY NOT NULL,
	pc_user int REFERENCES pc_user NOT NULL,
	role varchar(50) NOT NULL
);

CREATE TABLE if not exists pc_merchant_user(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int NOT NULL,
	user_id int NOT NULL
);

CREATE TABLE if not exists pc_reset_password (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	reset_code varchar(50) NOT NULL,
	email varchar(50) NOT NULL,
	status varchar(20) NOT NULL
);