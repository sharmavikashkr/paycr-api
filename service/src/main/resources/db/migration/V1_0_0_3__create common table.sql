CREATE TABLE if not exists pc_merchant_user(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int NOT NULL,
	user_id int NOT NULL
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

CREATE TABLE if not exists pc_report(
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(50) NOT NULL,
	time_range varchar(20) NOT NULL,
	pay_status varchar(20) NOT NULL,
	pay_type varchar(20) NOT NULL,
	pay_mode varchar(20) NOT NULL,
	merchant_id int REFERENCES pc_merchant
);