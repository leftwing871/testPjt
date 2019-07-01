package com.prd.consoletest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
        String host = "127.0.0.1";
        int port = 6379;
        int timeout = 3000;
        int db = 0;
        
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        
        JedisPool pool = new JedisPool(jedisPoolConfig,host,port,timeout,null,db);
        
        Jedis jedis = pool.getResource();
        
        //Connect 체크 
        System.out.println(jedis.isConnected());
        
        jedis.set("key4", "6");
        jedis.set("key5", "6");
        
        // 데이터의 만료시간을 지정
        jedis.expire("key5",1);
        
        System.out.println(jedis.get("key5"));
        
        try {
        	System.out.println("start thread sleep");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jedis.get("key5"));
    
        if( jedis != null ){
            jedis.close();
        }
        pool.close();
		
	}
}
