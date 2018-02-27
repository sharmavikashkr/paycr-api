package com.paycr.common.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.paycr.common.bean.Company;
import com.paycr.common.bean.Server;
import com.paycr.common.communicate.Email;
import com.paycr.common.communicate.EmailEngine;
import com.paycr.common.communicate.NotifyService;
import com.paycr.common.data.domain.InvoiceNote;
import com.paycr.common.data.domain.Merchant;
import com.paycr.common.type.NoteType;
import com.paycr.common.util.PdfUtil;

import freemarker.template.Configuration;

@Service
public class InvoiceNoteNotifyService implements NotifyService<InvoiceNote> {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceNoteNotifyService.class);

	@Autowired
	private EmailEngine emailEngine;

	@Autowired
	private Company company;

	@Autowired
	private Configuration fmConfiguration;

	@Autowired
	private Server server;

	@Autowired
	private PdfUtil pdfUtil;

	@Override
	public void notify(InvoiceNote note) {
		logger.info("Sending credit/debit note notification for note : {}", note.getNoteCode());
		Merchant merchant = note.getMerchant();
		List<String> to = new ArrayList<>();
		to.add(note.getConsumer().getEmail());
		List<String> cc = new ArrayList<>();
		Email email = new Email(company.getContactName(), company.getContactEmail(), company.getContactPassword(), to,
				cc);
		email.setSubject(note.getNoteType().name() + " NOTE for your invoice : " + note.getInvoiceCode());
		email.setMessage(
				"Hi " + note.getConsumer().getName() + ", please find attached credit/debit note for invoice : "
						+ note.getInvoiceCode() + " processed by " + merchant.getName());
		try {
			email.setMessage(getEmail(note));
		} catch (Exception ex) {
			logger.error("Execption while generating email body : {}", ex);
		}
		try {
			String fileName = note.getNoteCode() + ".pdf";
			String pdfPath = server.getInvoiceLocation() + fileName;
			File pdfFile = new File(pdfPath);
			if (!pdfFile.exists()) {
				pdfFile.createNewFile();
				pdfUtil.makePdf(company.getAppUrl() + "/note/receipt/" + note.getNoteCode(), pdfFile.getAbsolutePath());
			}
			email.setFileName(fileName);
			email.setFilePath(pdfPath);
		} catch (Exception ex) {
			logger.error("Execption while generating email pdf : {}", ex);
		}
		emailEngine.sendViaSES(email);
	}

	@Override
	public String getEmail(InvoiceNote note) throws Exception {
		Map<String, Object> templateProps = new HashMap<>();
		templateProps.put("note", note);
		templateProps.put("webUrl", company.getWebUrl());
		templateProps.put("staticUrl", company.getStaticUrl());
		if (NoteType.CREDIT.equals(note.getNoteType())) {
			templateProps.put("message", "Credit Note for your invoice from");
		} else {
			templateProps.put("message", "Debit Note for your invoice from");
		}
		return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate("email/note_email.ftl"),
				templateProps);
	}

	@Override
	public String getSms(InvoiceNote t) throws Exception {
		return null;
	}

}
