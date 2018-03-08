package com.paycr.common.bean.gst;

import java.util.ArrayList;
import java.util.List;

public class Gstr2Report {

	private List<Gstr2B2BR> b2bR;
	private List<Gstr2B2BUR> b2bUr;
	private List<Gstr2B2BRNote> b2bRNote;
	private List<Gstr2B2BURNote> b2bUrNote;
	private List<Gstr2Nil> nil;

	public Gstr2Report() {
		super();
		b2bR = new ArrayList<>();
		b2bUr = new ArrayList<>();
		b2bRNote = new ArrayList<>();
		b2bUrNote = new ArrayList<>();
		nil = new ArrayList<>();
	}

	public List<Gstr2B2BR> getB2bR() {
		return b2bR;
	}

	public void setB2bR(List<Gstr2B2BR> b2bR) {
		this.b2bR = b2bR;
	}

	public List<Gstr2B2BUR> getB2bUr() {
		return b2bUr;
	}

	public void setB2bUr(List<Gstr2B2BUR> b2bUr) {
		this.b2bUr = b2bUr;
	}

	public List<Gstr2B2BRNote> getB2bRNote() {
		return b2bRNote;
	}

	public void setB2bRNote(List<Gstr2B2BRNote> b2bRNote) {
		this.b2bRNote = b2bRNote;
	}

	public List<Gstr2B2BURNote> getB2bUrNote() {
		return b2bUrNote;
	}

	public void setB2bUrNote(List<Gstr2B2BURNote> b2bUrNote) {
		this.b2bUrNote = b2bUrNote;
	}

	public List<Gstr2Nil> getNil() {
		return nil;
	}

	public void setNil(List<Gstr2Nil> nil) {
		this.nil = nil;
	}

}
