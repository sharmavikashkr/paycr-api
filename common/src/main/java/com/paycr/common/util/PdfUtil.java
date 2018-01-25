package com.paycr.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paycr.common.bean.Server;

@Component
public class PdfUtil {

	private static final Logger logger = LoggerFactory.getLogger(PdfUtil.class);

	@Autowired
	private Server server;

	public void makePdf(String inputPath, String outputPath) {
		try {
			Process p = Runtime.getRuntime().exec(
					"xvfb-run " + server.getWkhtmlToPdfLocation() + "wkhtmltopdf" + " " + inputPath + " " + outputPath);
			p.waitFor();
		} catch (Exception ex) {
			logger.error("Execption while generating PDF ", ex);
		}
	}

}
