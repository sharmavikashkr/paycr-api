package com.payme.common.util;

import java.util.Random;

public class RandomIdGenerator {

	private static final String MTX_PREFIX = "INV";

	public static String generateMtx(char[] charset) {
		Random number = new Random();
		int length = number.nextInt(5) + 8;
		Random random = new Random();
		String randomCode = "";
		for (int i = 0; i < length; i++) {
			int idx = random.nextInt(charset.length);
			randomCode += charset[idx];
		}
		return MTX_PREFIX + randomCode;
	}

	public static String generateInvoiceCode(char[] charset) {
		Random number = new Random();
		int length = number.nextInt(5) + 8;
		Random random = new Random();
		String randomCode = "";
		for (int i = 0; i < length; i++) {
			int idx = random.nextInt(charset.length);
			randomCode += charset[idx];
		}
		return randomCode;
	}
}
