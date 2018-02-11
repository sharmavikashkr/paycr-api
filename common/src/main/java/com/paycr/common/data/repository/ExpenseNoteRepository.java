package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.data.domain.Merchant;

@Repository
public interface ExpenseNoteRepository extends JpaRepository<ExpenseNote, Integer> {

	@Query(value = "SELECT e FROM ExpenseNote e WHERE e.merchant = ?1 AND e.noteDate BETWEEN ?2 AND ?3")
	public List<ExpenseNote> findNotesForMerchant(Merchant merchant, Date startDate, Date endDate);

}
