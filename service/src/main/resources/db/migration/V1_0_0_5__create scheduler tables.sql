CREATE TABLE if not exists pc_recurring_invoice (
	id SERIAL PRIMARY KEY NOT NULL,
	recurr varchar(10) NOT NULL,
	start_date timestamp NOT NULL,
	next_date timestamp NOT NULL,
	total int NOT NULL,
	remaining int NOT NULL,
	active boolean NOT NULL,
    invoice_id int REFERENCES pc_invoice
);

CREATE TABLE if not exists pc_recurring_report (
	id SERIAL PRIMARY KEY NOT NULL,
	start_date timestamp NOT NULL,
	next_date timestamp NOT NULL,
	active boolean NOT NULL,
    report_id int REFERENCES pc_report,
    merchant_id int REFERENCES pc_merchant
);