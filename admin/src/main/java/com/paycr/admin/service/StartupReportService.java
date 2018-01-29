package com.paycr.admin.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.data.domain.Report;
import com.paycr.common.data.repository.ReportRepository;
import com.paycr.common.type.ReportType;
import com.paycr.common.type.TimeRange;
import com.paycr.common.util.CommonUtil;

@Component
public class StartupReportService {

	@Autowired
	private ReportRepository repRepo;

	public void createMasterReports() {
		if (CommonUtil.isNotEmpty(repRepo.findAll())) {
			return;
		}
		Date timeNow = new Date();
		List<Report> repList = new ArrayList<Report>();

		Report invRep = new Report();
		invRep.setCreated(timeNow);
		invRep.setDescription("Weekly report of invoice payments");
		invRep.setName("Invoice Report Weekly");
		invRep.setReportType(ReportType.INVOICE);
		invRep.setTimeRange(TimeRange.LAST_WEEK);
		repList.add(invRep);

		Report expRep = new Report();
		expRep.setCreated(timeNow);
		expRep.setDescription("Weekly report of expense payments");
		expRep.setName("Expense Report Weekly");
		expRep.setReportType(ReportType.EXPENSE);
		expRep.setTimeRange(TimeRange.LAST_WEEK);
		repList.add(expRep);

		Report conRep = new Report();
		conRep.setCreated(timeNow);
		conRep.setDescription("Monthly report of sales grouped by consumer");
		conRep.setName("Consumer Report Monthly");
		conRep.setReportType(ReportType.CONSUMER);
		conRep.setTimeRange(TimeRange.LAST_MONTH);
		repList.add(conRep);

		Report supRep = new Report();
		supRep.setCreated(timeNow);
		supRep.setDescription("Monthly report of purchases grouped by supplier");
		supRep.setName("Supplier Report Monthly");
		supRep.setReportType(ReportType.SUPPLIER);
		supRep.setTimeRange(TimeRange.LAST_MONTH);
		repList.add(supRep);

		Report invenRep = new Report();
		invenRep.setCreated(timeNow);
		invenRep.setDescription("Monthly report of sales grouped by inventory");
		invenRep.setName("Inventory Report Monthly");
		invenRep.setReportType(ReportType.INVENTORY);
		invenRep.setTimeRange(TimeRange.LAST_MONTH);
		repList.add(invenRep);

		Report astRep = new Report();
		astRep.setCreated(timeNow);
		astRep.setDescription("Monthly report of purchases grouped by asset");
		astRep.setName("Asser Report Monthly");
		astRep.setReportType(ReportType.ASSET);
		astRep.setTimeRange(TimeRange.LAST_MONTH);
		repList.add(astRep);

		repRepo.save(repList);
	}

}
