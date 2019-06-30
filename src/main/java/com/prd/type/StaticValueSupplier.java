package com.prd.type;


public class StaticValueSupplier {
	static final String decrypt_db_account_seoul = "arn:aws:lambda:ap-northeast-2:628478222617:function:decrypt_db_account_seoul_ssb";
	static final String decrypt_db_account_california = "";
	
	public static String getARNdecrypt_db_account(final String zone) {
		if(zone.equals("DEV") || zone.equals("QA") || zone.equals("QABETA") || zone.equals("LIVE"))
			return decrypt_db_account_seoul;
		else
			return decrypt_db_account_california;
	}
}
