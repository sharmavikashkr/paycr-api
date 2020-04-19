package com.paycr.common.bean.search;

import lombok.Data;

@Data
public class SearchSupplierRequest {

	private Integer merchant;
	private String name;
	private String email;
	private String mobile;

}
