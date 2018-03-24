package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface InvoiceNoteRepository extends JpaRepository<InvoiceNote, Integer> {

	@Query(value = "SELECT i FROM InvoiceNote i WHERE i.merchant = ?1 AND i.noteDate BETWEEN ?2 AND ?3 AND i.deleted = FALSE")
	public List<InvoiceNote> findNotesForMerchant(Merchant merchant, Date startDate, Date endDate);

}
