/**
 * @file Redis.java
 * @author wangsha
 * @date Oct 10, 2011 
 */
package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import object.Schema;

import redis.clients.jedis.Jedis;

/**
 * @author wangsha
 * 
 */
public class Redis extends Jedis{

	public Redis(String host) {
		super(host);
	}

	
	public void sadd(String key, Set<String> values) {
		Iterator<String> it = values.iterator();
		while(it.hasNext()) {
			super.sadd(key, it.next());
		}
	}
	
	public void hset(String key, Schema entity, String[] values) throws SQLException {
		String[] attri = entity.getAttributes();
		for(int i=0; i<attri.length; i++) {
			String val =  values[i];
			
			super.hset(key, attri[i], val);
		}
	}
	
	public void hsetall(String key, String[] attributes, String values[]) throws SQLException {
		assert(attributes.length == values.length);
		for(int i=0; i<attributes.length; i++) {
			super.hset(key, attributes[i], values[i]);
		}
	}

}
