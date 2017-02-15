package com.payme.common.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

@Component
public class HmacSignerUtil {

	private final String signAlgo = "HmacSHA1";

	@SuppressWarnings("hiding")
	public String signWithSecretKey(String secret, String data) {
		byte[] signature;
		try {
			SecretKeySpec k = new SecretKeySpec(secret.getBytes(), signAlgo);
			Mac mac = Mac.getInstance(signAlgo);
			mac.init(k);
			signature = new Hex().encode(mac.doFinal(data.getBytes()));
		} catch (InvalidKeyException ikx) {
			throw new RuntimeException(ikx);
		} catch (NoSuchAlgorithmException nsax) {
			throw new RuntimeException(nsax);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
		try {
			return new String(signature, "UTF-8");
		} catch (UnsupportedEncodingException uex) {
			throw new RuntimeException(uex);
		}
	}

}
