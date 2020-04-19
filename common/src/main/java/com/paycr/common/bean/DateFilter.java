package com.paycr.common.bean;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateFilter {

	private Date startDate;
	private Date endDate;

}
