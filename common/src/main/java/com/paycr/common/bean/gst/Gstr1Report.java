package com.paycr.common.bean.gst;

import java.util.ArrayList;
import java.util.List;

public class Gstr1Report {

	private List<Gstr1B2CSmall> b2cSmall;
	private List<Gstr1B2CLarge> b2cLarge;
	private List<Gstr1B2B> b2b;
	private List<Gstr1B2BNote> b2bNote;
	private List<Gstr1B2CNote> b2cNote;
	private List<Gstr1Nil> nil;

	public Gstr1Report() {
		super();
		b2cSmall = new ArrayList<>();
		b2cLarge = new ArrayList<>();
		b2b = new ArrayList<>();
		b2bNote = new ArrayList<>();
		b2cNote = new ArrayList<>();
		nil = new ArrayList<>();
	}

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

	public List<Gstr1B2BNote> getB2bNote() {
		return b2bNote;
	}

	public void setB2bNote(List<Gstr1B2BNote> b2bNote) {
		this.b2bNote = b2bNote;
	}

	public List<Gstr1B2CNote> getB2cNote() {
		return b2cNote;
	}

	public void setB2cNote(List<Gstr1B2CNote> b2cNote) {
		this.b2cNote = b2cNote;
	}

	public List<Gstr1Nil> getNil() {
		return nil;
	}

	public void setNil(List<Gstr1Nil> nil) {
		this.nil = nil;
	}

}
