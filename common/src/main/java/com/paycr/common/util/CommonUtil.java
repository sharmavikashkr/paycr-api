package com.paycr.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpStatus;

import com.paycr.common.exception.PaycrException;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

public class CommonUtil {

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final String MOBILE_PATTERN = "^[7-9]{1}[0-9]{9}$";

	public static final String NAME_PATTERN = "[a-zA-Z ]{1,50}";

	public static final String GSTIN_PATTERN = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";

	public static final String INVOICE_CODE_PATTERN = "[a-zA-Z0-9-/ ]{3,16}";

	public static String base64Decode(String val) throws PaycrException {
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(val.getBytes());
			return new String(decodedBytes, Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new PaycrException(HttpStatus.SC_BAD_REQUEST, "Error while decoding string = " + val);
		}
	}

	public static String base64Encode(String val) {
		byte[] encodedBytes = Base64.getEncoder().encode(val.getBytes());
		return new String(encodedBytes, Charset.forName("UTF-8"));
	}

	public static List<String[]> readCSV(InputStream inputStream) throws IOException {
		Reader reader = new InputStreamReader(inputStream);
		CSVReader csvReader = new CSVReader(reader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);
		List<String[]> list = csvReader.readAll();
		csvReader.close();
		return list;
	}

	public static boolean isNotNull(Object object) {
		return (object != null) ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean isNull(Object object) {
		return (object == null) ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean isEmpty(String str) {
		if (isNull(str)) {
			return true;
		} else if ("".equals(str.trim())) {
			return true;
		}
		return false;
	}

	public static <T> boolean isNotEmpty(Collection<T> objList) {
		return (isNotNull(objList) && (!objList.isEmpty()));
	}

	public static <T> boolean isEmpty(Collection<T> objList) {
		return (isNull(objList) || (objList.isEmpty()));
	}

	public static String formatTwoDecimalPlaces(double value) {
		return new DecimalFormat("#0.00").format(value);
	}

	public static boolean match(String value, String pattern) {
		if (CommonUtil.isNotNull(value)) {
			return value.matches(pattern);
		}
		return true;
	}

}
