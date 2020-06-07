package com.paycr.common.bean.gst;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2Report {

	private String period;
	private List<Gstr2B2BR> b2bR;
	private List<Gstr2B2BUR> b2bUr;
	private List<Gstr2B2BRNote> b2bRNote;
	private List<Gstr2B2BURNote> b2bUrNote;
	private List<Gstr2Nil> nil;

}
