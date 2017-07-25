package com.paycr.common.util;

public class RoleUtil {

	public static final String ALL_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')"
			+ " or hasAuthority('ROLE_PAYCR_FINANCE') or hasAuthority('ROLE_PAYCR_OPS') or hasAuthority('ROLE_PAYCR_ADVISOR')"
			+ " or hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_FINANCE') or hasAuthority('ROLE_MERCHANT_OPS')";

	public static final String ALL_ADMIN_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')"
			+ " or hasAuthority('ROLE_MERCHANT')";
	
	public static final String ALL_FINANCE_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')"
			+ " or hasAuthority('ROLE_PAYCR_FINANCE') or hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_FINANCE')";
	
	public static final String ALL_OPS_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')"
			+ " or hasAuthority('ROLE_PAYCR_OPS') or hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_OPS')";

	public static final String PAYCR_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')"
			+ " or hasAuthority('ROLE_PAYCR_FINANCE') or hasAuthority('ROLE_PAYCR_OPS') or hasAuthority('ROLE_PAYCR_ADVISOR')";

	public static final String MERCHANT_AUTH = "hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_FINANCE')"
			+ " or hasAuthority('ROLE_MERCHANT_OPS')";

	public static final String PAYCR_ADMIN_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')";

	public static final String PAYCR_FINANCE_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')"
			+ " or hasAuthority('ROLE_PAYCR_FINANCE')";

	public static final String MERCHANT_FINANCE_AUTH = "hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_FINANCE')";

	public static final String PAYCR_OPS_AUTH = "hasAuthority('ROLE_PAYCR') or hasAuthority('ROLE_PAYCR_SUPERVISOR')"
			+ " or hasAuthority('ROLE_PAYCR_OPS')";

	public static final String MERCHANT_OPS_AUTH = "hasAuthority('ROLE_MERCHANT') or hasAuthority('ROLE_MERCHANT_OPS')";

}
