CREATE TABLE if not exists pc_user (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) DEFAULT NULL,
    email varchar(50) DEFAULT NULL,
    mobile varchar(15) NOT NULL,
    created_by varchar(50) NOT NULL,
    user_type varchar(20) NOT NULL,
    password varchar(100) NOT NULL,
    address_line1 varchar(255) DEFAULT NULL,
    address_line2 varchar(255) DEFAULT NULL,
    city varchar(30) DEFAULT NULL,
    district varchar(30) DEFAULT NULL,
    state varchar(50) DEFAULT NULL,
    country varchar(50) DEFAULT NULL,
    pincode varchar(10) DEFAULT NULL,
    active boolean NOT NULL
);

CREATE TABLE if not exists pc_user_role(
	id SERIAL PRIMARY KEY NOT NULL,
	pc_user int REFERENCES pc_user,
	role varchar(50) NOT NULL
);

CREATE TABLE if not exists pc_reset_password (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	reset_code varchar(50) NOT NULL,
	email varchar(50) NOT NULL,
	status varchar(20) NOT NULL
);