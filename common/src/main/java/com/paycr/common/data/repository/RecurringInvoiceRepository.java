package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.RecurringInvoice;

@Repository
public interface RecurringInvoiceRepository extends JpaRepository<RecurringInvoice, Integer> {

	public List<RecurringInvoice> findByInvoice(Invoice invoice);

	public RecurringInvoice findByInvoiceAndActive(Invoice invoice, boolean active);

	@Query("select ri from RecurringInvoice ri where nextDate between ?1 and ?2")
	public List<RecurringInvoice> findTodaysRecurringInvoices(Date start, Date end);

}
