package com.paycr.common.bean.gst;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr1Report {

	private String period;
	private List<Gstr1B2CSmall> b2cSmall;
	private List<Gstr1B2CLarge> b2cLarge;
	private List<Gstr1B2B> b2b;
	private List<Gstr1B2BNote> b2bNote;
	private List<Gstr1B2CNote> b2cNote;
	private List<Gstr1Nil> nil;

}
