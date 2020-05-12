CREATE TABLE if not exists pc_recurring_invoice (
	id SERIAL PRIMARY KEY NOT NULL,
	recurr varchar(10) NOT NULL,
	start_date timestamp NOT NULL,
	next_date timestamp NOT NULL,
	total int NOT NULL,
	remaining int NOT NULL,
	active boolean NOT NULL,
    invoice_id int REFERENCES pc_invoice NOT NULL
);

CREATE TABLE if not exists pc_schedule (
	id SERIAL PRIMARY KEY NOT NULL,
	start_date timestamp NOT NULL,
	next_date timestamp NOT NULL,
	active boolean NOT NULL,
    report_id int REFERENCES pc_report NOT NULL,
    merchant_id int REFERENCES pc_merchant
);

CREATE TABLE if not exists pc_schedule_user (
	id SERIAL PRIMARY KEY NOT NULL,
    schedule_id int REFERENCES pc_schedule NOT NULL,
    pc_user_id int REFERENCES pc_user NOT NULL
);

CREATE TABLE if not exists pc_schedule_history (
	id SERIAL PRIMARY KEY NOT NULL,
    schedule_id int REFERENCES pc_schedule NOT NULL,
    from_date timestamp NOT NULL,
	to_date timestamp NOT NULL,
	created timestamp NOT NULL,
	status varchar(15) NOT NULL
);