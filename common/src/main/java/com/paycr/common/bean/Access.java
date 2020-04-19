package com.paycr.common.bean;

import lombok.Data;

@Data
public class Access {

	private boolean admin;
	private boolean supervisor;
	private boolean finance;
	private boolean ops;
	private boolean advisor;

}
