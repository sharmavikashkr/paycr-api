CREATE TABLE if not exists pc_notification(
	id SERIAL PRIMARY KEY NOT NULL,
	merchant_id int DEFAULT NULL,
	user_id int DEFAULT NULL,
	subject varchar(50) NOT NULL,
	message varchar(255) NOT NULL,
	created timestamp NOT NULL,
	read boolean NOT NULL
);