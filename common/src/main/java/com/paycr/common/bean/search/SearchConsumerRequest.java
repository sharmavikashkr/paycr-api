package com.paycr.common.bean.search;

import java.util.List;

import com.paycr.common.data.domain.ConsumerFlag;

import lombok.Data;

@Data
public class SearchConsumerRequest {

	private Integer merchant;
	private String name;
	private String email;
	private String mobile;
	private List<ConsumerFlag> flagList;

}
