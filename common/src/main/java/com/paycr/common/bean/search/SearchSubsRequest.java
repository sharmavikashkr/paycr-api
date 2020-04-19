package com.paycr.common.bean.search;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

import com.paycr.common.type.PayMode;

@Data
public class SearchSubsRequest {

	private Integer merchant;
	private Integer pricing;
	private PayMode payMode;
	private String subsCode;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdFrom;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdTo;

}
