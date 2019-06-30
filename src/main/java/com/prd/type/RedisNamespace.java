package com.prd.type;

import lombok.Getter;

@Getter
public enum RedisNamespace {
	SSB_PLAYTRANSACTION("ssb:playtransaction"),
	MUSIC_RANKING("ssb:musicRanking:"),
	SSB_TEST1("ssb:test1");
	
	private String namespace;
	
	private RedisNamespace(String namespace)
	{
		this.namespace = namespace;
	}
	
	public static String getKey(RedisNamespace namespace, String key)
	{
		return namespace.getNamespace() + key;
	}
}
