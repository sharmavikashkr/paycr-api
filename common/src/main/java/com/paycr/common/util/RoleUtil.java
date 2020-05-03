package com.paycr.common.util;

public class RoleUtil {

	public static final String ALL_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_PAYCR_FINANCE','ROLE_PAYCR_OPS','ROLE_PAYCR_ADVISOR','ROLE_MERCHANT','ROLE_MERCHANT_FINANCE','ROLE_MERCHANT_OPS')";

	public static final String ALL_ADMIN_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_MERCHANT')";

	public static final String ALL_FINANCE_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_PAYCR_FINANCE','ROLE_MERCHANT','ROLE_MERCHANT_FINANCE')";

	public static final String ALL_OPS_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_PAYCR_OPS','ROLE_MERCHANT','ROLE_MERCHANT_OPS')";

	public static final String PAYCR_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_PAYCR_FINANCE','ROLE_PAYCR_OPS','ROLE_PAYCR_ADVISOR')";

	public static final String MERCHANT_AUTH = "hasAnyAuthority('ROLE_MERCHANT','ROLE_MERCHANT_FINANCE','ROLE_MERCHANT_OPS')";

	public static final String PAYCR_ADMIN_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR')";

	public static final String MERCHANT_ADMIN_AUTH = "hasAnyAuthority('ROLE_MERCHANT')";

	public static final String PAYCR_FINANCE_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_PAYCR_FINANCE')";

	public static final String MERCHANT_FINANCE_AUTH = "hasAnyAuthority('ROLE_MERCHANT','ROLE_MERCHANT_FINANCE')";

	public static final String PAYCR_OPS_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_PAYCR_OPS')";

	public static final String MERCHANT_OPS_AUTH = "hasAnyAuthority('ROLE_MERCHANT','ROLE_MERCHANT_OPS')";

	public static final String PAYCR_ADVISOR_AUTH = "hasAnyAuthority('ROLE_PAYCR','ROLE_PAYCR_SUPERVISOR','ROLE_PAYCR_ADVISOR')";

}
