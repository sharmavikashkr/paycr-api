CREATE TABLE if not exists pc_recurring_invoice (
	id SERIAL PRIMARY KEY NOT NULL,
	start_date timestamp NOT NULL,
	next_inv_date timestamp NOT NULL,
	total int NOT NULL,
	remaining int NOT NULL,
	active boolean NOT NULL,
    invoice_id int REFERENCES pc_invoice
);