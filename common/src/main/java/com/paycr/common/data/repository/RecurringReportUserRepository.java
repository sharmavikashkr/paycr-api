package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paycr.common.data.domain.PcUser;
import com.paycr.common.data.domain.RecurringReport;
import com.paycr.common.data.domain.RecurringReportUser;

@Repository
public interface RecurringReportUserRepository extends JpaRepository<RecurringReportUser, Integer> {

	public List<RecurringReportUser> findByPcUser(PcUser user);

	public List<RecurringReportUser> findByRecurringReport(RecurringReport recurringReport);

	public RecurringReportUser findByRecurringReportAndPcUser(RecurringReport recurringReport, PcUser user);

}
