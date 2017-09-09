package com.paycr.common.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.domain.RecurringReport;
import com.paycr.common.data.domain.Report;

@Repository
public interface RecurringReportRepository extends JpaRepository<RecurringReport, Integer> {

	@Query("SELECT rr FROM RecurringReport rr WHERE active = true AND nextDate BETWEEN ?1 and ?2")
	public List<RecurringReport> findTodaysRecurringReports(Date start, Date end);

	public RecurringReport findByReportAndMerchant(Report report, Merchant merchant);

}
