package com.paycr.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

public class CommonUtil {

	public static String base64Decode(String val) throws Exception {
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(val.getBytes());
			return new String(decodedBytes, Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new Exception("Error while decoding string = " + val);
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

	public static <T> boolean isNotEmpty(List<T> objList) {
		return (isNotNull(objList) && (objList.size() > 0));
	}

	public static <T> boolean isNotEmpty(Set<T> objList) {
		return (isNotNull(objList) && (objList.size() > 0));
	}

	public static <T> boolean isEmpty(List<T> objList) {
		return (isNull(objList) || (objList.size() == 0));
	}

	public static String handleNull(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}

	public static String formatTwoDecimalPlaces(double value) {
		return new DecimalFormat("#0.00").format(value);
	}

}
