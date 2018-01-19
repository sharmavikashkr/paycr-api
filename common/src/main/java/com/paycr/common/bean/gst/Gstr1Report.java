package com.paycr.common.bean.gst;

import java.util.List;

public class Gstr1Report {

	private List<Gstr1B2CSmall> b2cSmall;
	private List<Gstr1B2CLarge> b2cLarge;
	private List<Gstr1B2B> b2b;
	private List<Gstr1B2BCDNote> b2bCDNote;
	private List<Gstr1B2CCDNote> b2cCDNote;

	public List<Gstr1B2CSmall> getB2cSmall() {
		return b2cSmall;
	}

	public void setB2cSmall(List<Gstr1B2CSmall> b2cSmall) {
		this.b2cSmall = b2cSmall;
	}

	public List<Gstr1B2CLarge> getB2cLarge() {
		return b2cLarge;
	}

	public void setB2cLarge(List<Gstr1B2CLarge> b2cLarge) {
		this.b2cLarge = b2cLarge;
	}

	public List<Gstr1B2B> getB2b() {
		return b2b;
	}

	public void setB2b(List<Gstr1B2B> b2b) {
		this.b2b = b2b;
	}

	public List<Gstr1B2BCDNote> getB2bCDNote() {
		return b2bCDNote;
	}

	public void setB2bCDNote(List<Gstr1B2BCDNote> b2bCDNote) {
		this.b2bCDNote = b2bCDNote;
	}

	public List<Gstr1B2CCDNote> getB2cCDNote() {
		return b2cCDNote;
	}

	public void setB2cCDNote(List<Gstr1B2CCDNote> b2cCDNote) {
		this.b2cCDNote = b2cCDNote;
	}

}
