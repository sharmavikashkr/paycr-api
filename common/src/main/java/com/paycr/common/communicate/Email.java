package com.paycr.common.communicate;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Email {

	private String name;
	private String from;
	private String password;
	private List<String> to;
	private List<String> cc;
	private String subject;
	private String message;
	private String filePath;
	private String fileName;

	public Email(String name, String from, String password, List<String> to, List<String> cc) {
		this.name = name;
		this.from = from;
		this.password = password;
		this.to = to;
		this.cc = cc;
	}

}
