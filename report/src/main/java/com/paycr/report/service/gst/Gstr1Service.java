package com.paycr.report.service.gst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.bean.gst.Gstr1Report;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.data.domain.GstSetting;
import com.paycr.common.data.domain.Invoice;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.data.repository.InvoiceNoteRepository;
import com.paycr.common.data.repository.InvoiceRepository;
import com.paycr.common.type.FilingPeriod;
import com.paycr.common.type.InvoiceStatus;
import com.paycr.common.util.DateUtil;

@Service
public class Gstr1Service {

	@Autowired
	private InvoiceRepository invRepo;

	@Autowired
	private InvoiceNoteRepository invNoteRepo;

	@Autowired
	private Gstr1B2BService b2bSer;

	@Autowired
	private Gstr1B2CLargeService b2cLargeSer;

	@Autowired
	private Gstr1B2CSmallService b2cSmallSer;

	@Autowired
	private Gstr1B2CNoteService b2cNoteSer;

	@Autowired
	private Gstr1B2BNoteService b2bNoteSer;

	@Autowired
	private Company company;

	@Autowired
	private Server server;

	@Autowired
	private EmailEngine emailEngine;

	public Gstr1Report loadGstr1Report(Merchant merchant, String periodStr) throws Exception {
		Gstr1Report gstr1Report = new Gstr1Report();
		Date start = null;
		Date end = null;
		String[] periodArr = periodStr.split("-");
		if (FilingPeriod.MONTHLY.equals(merchant.getGstSetting().getFilingPeriod())) {
			String month = periodArr[0];
			String year = periodArr[1];
			Date aDayInMonth = DateUtil.parseDefaultDate(year + "-" + month + "-15");
			start = DateUtil.getFirstDayOfMonth(aDayInMonth);
			end = DateUtil.getLastDayOfMonth(aDayInMonth);
		} else if (FilingPeriod.QUARTERLY.equals(merchant.getGstSetting().getFilingPeriod())) {
			String fstMonth = periodArr[0];
			String lstMonth = periodArr[1];
			String year = periodArr[2];
			Date aDayInFirstMonth = DateUtil.parseDefaultDate(year + "-" + fstMonth + "-15");
			start = DateUtil.getFirstDayOfMonth(aDayInFirstMonth);
			Date aDayInLastMonth = DateUtil.parseDefaultDate(year + "-" + lstMonth + "-15");
			end = DateUtil.getLastDayOfMonth(aDayInLastMonth);
		}
		start = DateUtil.getISTTimeInUTC(start);
		end = DateUtil.getISTTimeInUTC(end);
		List<InvoiceStatus> gstStatuses = new ArrayList<InvoiceStatus>();
		GstSetting gstSet = merchant.getGstSetting();
		if (gstSet.isInvCreated()) {
			gstStatuses.add(InvoiceStatus.CREATED);
		}
		if (gstSet.isInvDeclined()) {
			gstStatuses.add(InvoiceStatus.DECLINED);
		}
		if (gstSet.isInvExpired()) {
			gstStatuses.add(InvoiceStatus.EXPIRED);
		}
		if (gstSet.isInvPaid()) {
			gstStatuses.add(InvoiceStatus.PAID);
		}
		if (gstSet.isInvUnpaid()) {
			gstStatuses.add(InvoiceStatus.UNPAID);
		}
		List<Invoice> invoiceList = invRepo.findInvoicesForMerchant(merchant, gstStatuses, start, end);
		List<InvoiceNote> noteList = invNoteRepo.findNotesForMerchant(merchant, start, end);
		gstr1Report.setB2cLarge(b2cLargeSer.collectB2CLargeList(invoiceList));
		gstr1Report.setB2cSmall(b2cSmallSer.collectB2CSmallList(invoiceList, noteList));
		gstr1Report.setB2b(b2bSer.collectB2BList(invoiceList));
		gstr1Report.setB2cNote(b2cNoteSer.collectB2CNoteList(noteList));
		gstr1Report.setB2bNote(b2bNoteSer.collectB2BNoteList(noteList));
		return gstr1Report;
	}

	public byte[] downloadGstr1Report(Merchant merchant, String periodStr) throws Exception {
		String zipFilePath = getAssembledZipFilePath(merchant, periodStr);
		FileInputStream fis = new FileInputStream(zipFilePath);
		return IOUtils.toByteArray(fis);
	}

	public void mailGstr1Report(String recepient, Merchant merchant, String periodStr) throws Exception {
		String fileName = "GSTR1 Report - " + periodStr + ".zip";
		String filePath = getAssembledZipFilePath(merchant, periodStr);
		List<String> to = new ArrayList<>();
		to.add(recepient);
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject("GSTR1 Report - " + periodStr);
		email.setMessage("GSTR1 Report - " + periodStr);
		email.setFileName(fileName);
		email.setFilePath(filePath);
		emailEngine.sendViaGmail(email);

	}

	private String getAssembledZipFilePath(Merchant merchant, String periodStr) throws Exception {
		Gstr1Report gstr1Report = loadGstr1Report(merchant, periodStr);
		String zipFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + ".zip";
		FileOutputStream fos = new FileOutputStream(zipFilePath);
		ZipOutputStream zos = new ZipOutputStream(fos);

		String b2bCsv = b2bSer.getB2BCsv(gstr1Report.getB2b());
		String csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2B.csv";
		addDateToZip(zos, b2bCsv, csvFilePath, "B2B.csv");

		String b2cLargeCsv = b2cLargeSer.getB2CLargeCsv(gstr1Report.getB2cLarge());
		csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2CLarge.csv";
		addDateToZip(zos, b2cLargeCsv, csvFilePath, "B2CLarge.csv");

		String b2cSmallCsv = b2cSmallSer.getB2CSmallCsv(gstr1Report.getB2cSmall());
		csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2CSmall.csv";
		addDateToZip(zos, b2cSmallCsv, csvFilePath, "B2CSmall.csv");

		String b2bNoteCsv = b2bNoteSer.getB2BNoteCsv(gstr1Report.getB2bNote());
		csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2BNote.csv";
		addDateToZip(zos, b2bNoteCsv, csvFilePath, "B2BNote.csv");

		String b2cNoteCsv = b2cNoteSer.getB2CNoteCsv(gstr1Report.getB2cNote());
		csvFilePath = server.getGstLocation() + merchant.getAccessKey() + " - " + periodStr + " B2CNote.csv";
		addDateToZip(zos, b2cNoteCsv, csvFilePath, "B2CNote.csv");

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
