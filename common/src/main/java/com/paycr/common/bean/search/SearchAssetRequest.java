package com.paycr.common.bean.search;

import lombok.Data;

@Data
public class SearchAssetRequest {

	private Integer merchant;
	private String code;
	private String name;

}
