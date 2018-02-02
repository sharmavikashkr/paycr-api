CREATE TABLE if not exists pc_contact_us (
	id SERIAL PRIMARY KEY NOT NULL,
	created timestamp NOT NULL,
	name varchar(100) NOT NULL,
    email varchar(50) NOT NULL,
    type varchar(30) NOT NULL,
    resolved boolean NOT NULL,
    subject varchar(100) NOT NULL,
    message TEXT NOT NULL
);