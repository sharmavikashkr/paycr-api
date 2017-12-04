package com.paycr.common.util;

import java.util.Random;

public class RandomIdGenerator {

	private static final String MTX_PREFIX = "INV";

	public static String generateInvoiceCode(char[] charset) {
		Random random = new Random();
		String randomCode = "";
		for (int i = 0; i < 11; i++) {
			int idx = random.nextInt(charset.length);
			randomCode += charset[idx];
		}
		return randomCode;
	}

	public static String generateMtx(char[] charset) {
		Random random = new Random();
		String randomCode = "";
		for (int i = 0; i < 11; i++) {
			int idx = random.nextInt(charset.length);
			randomCode += charset[idx];
		}
		return MTX_PREFIX + randomCode;
	}

	public static String generateAccessKey(char[] charset) {
		Random random = new Random();
		String randomCode = "";
		for (int i = 0; i < 12; i++) {
			int idx = random.nextInt(charset.length);
			randomCode += charset[idx];
		}
		return randomCode;
	}
}
