package com.paycr.report.service.gst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.bean.gst.Gstr2Report;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.Expense;
import com.paycr.common.data.domain.ExpenseNote;
import com.paycr.common.data.domain.GstSetting;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.ExpenseNoteRepository;
import com.paycr.common.data.repository.ExpenseRepository;
import com.paycr.common.type.ExpenseStatus;
import com.paycr.common.util.CommonUtil;
import com.paycr.common.util.DateUtil;

@Service
public class Gstr2Service {

	@Autowired
	private ExpenseRepository expRepo;

	@Autowired
	private ExpenseNoteRepository expNoteRepo;

	@Autowired
	private Gstr2B2BRService b2bRSer;

	@Autowired
	private Gstr2B2BURService b2bUrSer;

	@Autowired
	private Gstr2B2BURNoteService b2bUrNoteSer;

	@Autowired
	private Gstr2B2BRNoteService b2bRNoteSer;

	@Autowired
	private Company company;

	@Autowired
	private Server server;

	@Autowired
	private EmailEngine emailEngine;

	@Transactional
	public Gstr2Report loadGstr2Report(Merchant merchant, String periodStr) throws Exception {
		Date start = null;
		Date end = null;
		String[] periodArr = periodStr.split("-");
		String month = periodArr[0];
		String year = periodArr[1];
		Date aDayInMonth = DateUtil.parseDefaultDate(year + "-" + month + "-15");
		start = DateUtil.getISTTimeInUTC(DateUtil.getFirstDayOfMonth(aDayInMonth));
		end = DateUtil.getISTTimeInUTC(DateUtil.getLastDayOfMonth(aDayInMonth));
		List<ExpenseStatus> gstStatuses = new ArrayList<ExpenseStatus>();
		GstSetting gstSet = merchant.getGstSetting();
		if (gstSet.isExpUnpaid()) {
			gstStatuses.add(ExpenseStatus.UNPAID);
		}
		if (gstSet.isExpPaid()) {
			gstStatuses.add(ExpenseStatus.PAID);
		}
		List<Expense> expenseList = expRepo.findExpensesForMerchant(merchant, gstStatuses, start, end);
		expenseList = expenseList.stream().filter(t -> CommonUtil.isNotEmpty(t.getItems()))
				.collect(Collectors.toList());
		List<ExpenseNote> noteList = expNoteRepo.findNotesForMerchant(merchant, start, end);
		List<Future<Boolean>> collectFutures = new ArrayList<Future<Boolean>>();
		Gstr2Report gstr2Report = new Gstr2Report();
		collectFutures.add(b2bUrSer.collectB2BUrList(gstr2Report, expenseList));
		collectFutures.add(b2bRSer.collectB2BRList(gstr2Report, expenseList));
		collectFutures.add(b2bUrNoteSer.collectB2BUrNoteList(gstr2Report, noteList));
		collectFutures.add(b2bRNoteSer.collectB2BRNoteList(gstr2Report, noteList));
		for (Future<Boolean> collectFuture : collectFutures) {
			while (!collectFuture.isDone() && !collectFuture.isCancelled()) {
			}
		}
		return gstr2Report;
	}

	public byte[] downloadGstr2Report(Merchant merchant, String periodStr) throws Exception {
		String zipFilePath = getAssembledZipFilePath(merchant, periodStr);
		FileInputStream fis = new FileInputStream(zipFilePath);
		return IOUtils.toByteArray(fis);
	}

	@Async
	@Transactional
	public void mailGstr2Report(String recepient, Merchant merchant, String periodStr) throws Exception {
		String fileName = "GSTR2 Report - " + periodStr + ".zip";
		String filePath = getAssembledZipFilePath(merchant, periodStr);
		List<String> to = new ArrayList<>();
		to.add(recepient);
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("GSTR2 Report - " + periodStr);
		email.setMessage("GSTR2 Report - " + periodStr);
		email.setFileName(fileName);
		email.setFilePath(filePath);
		emailEngine.sendViaGmail(email);

	}

	private String getAssembledZipFilePath(Merchant merchant, String periodStr) throws Exception {
		Gstr2Report gstr2Report = loadGstr2Report(merchant, periodStr);
		String zipFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + ".zip";
		FileOutputStream fos = new FileOutputStream(zipFilePath);
		ZipOutputStream zos = new ZipOutputStream(fos);

		String b2bRCsv = b2bRSer.getB2BRCsv(gstr2Report.getB2bR());
		String csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2BR.csv";
		addDateToZip(zos, b2bRCsv, csvFilePath, "B2BR.csv");

		String b2bUrCsv = b2bUrSer.getB2BUrCsv(gstr2Report.getB2bUr());
		csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2BUR.csv";
		addDateToZip(zos, b2bUrCsv, csvFilePath, "B2BUR.csv");

		String b2bRNoteCsv = b2bRNoteSer.getB2BRNoteCsv(gstr2Report.getB2bRNote());
		csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2BRNote.csv";
		addDateToZip(zos, b2bRNoteCsv, csvFilePath, "B2BRNote.csv");

		String b2bUrNoteCsv = b2bUrNoteSer.getB2BUrNoteCsv(gstr2Report.getB2bUrNote());
		csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2BURNote.csv";
		addDateToZip(zos, b2bUrNoteCsv, csvFilePath, "B2BURNote.csv");

		zos.closeEntry();
		zos.close();
		fos.close();
		return zipFilePath;
	}

	private void addDateToZip(ZipOutputStream zos, String data, String filePath, String fileName) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(data.getBytes());
		out.close();
		byte[] buffer = new byte[1024];
		ZipEntry ze = new ZipEntry(fileName);
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(filePath);
		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		in.close();
	}

}
