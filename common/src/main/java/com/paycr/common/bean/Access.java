package com.paycr.common.bean;

public class Access {

	private boolean admin;
	private boolean supervisor;
	private boolean finance;
	private boolean ops;
	private boolean advisor;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isSupervisor() {
		return supervisor;
	}

	public void setSupervisor(boolean supervisor) {
		this.supervisor = supervisor;
	}

	public boolean isFinance() {
		return finance;
	}

	public void setFinance(boolean finance) {
		this.finance = finance;
	}

	public boolean isOps() {
		return ops;
	}

	public void setOps(boolean ops) {
		this.ops = ops;
	}

	public boolean isAdvisor() {
		return advisor;
	}

	public void setAdvisor(boolean advisor) {
		this.advisor = advisor;
	}

}
