package com.paycr.common.bean;

import java.util.List;

import com.paycr.common.bean.search.SearchConsumerRequest;
import com.paycr.common.data.domain.ConsumerFlag;

import lombok.Data;

@Data
public class UpdateConsumerRequest {

	private boolean emailOnPay;
	private boolean emailOnRefund;
	private boolean emailNote;
	private boolean active;
	private boolean removeOldTags;
	private List<ConsumerFlag> flagList;
	private SearchConsumerRequest searchReq;

}
