CREATE TABLE if not exists pc_subscription(
	id SERIAL PRIMARY KEY NOT NULL,
	subscription_code int NOT NULL,
	amount float NOT NULL,
	currency varchar(10) NOT NULL,
	created timestamp NOT NULL,
	payment_ref_no varchar(50) NOT NULL,
	status varchar(20) NOT NULL,
	method varchar(20) NOT NULL,
	bank varchar(20) DEFAULT NULL,
	wallet varchar(20) DEFAULT NULL,
	merchant_id int REFERENCES pc_merchant,
    pricing_id int REFERENCES pc_pricing
);