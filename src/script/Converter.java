/**
 * @file MySQLDataImporter.java
 * @author wangsha
 * @date Oct 4, 2011 
 */
package script;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import object.DatabaseStructure;
import object.Schema;

import database.MySQLConnector;
import database.Redis;

/**
 * @author wangsha
 * 
 */
public class Converter {
	private MySQLConnector sql;
	private Redis redis;
	private DatabaseStructure sql_db;
	

	public Converter(MySQLConnector sql, Redis redis) {
		this.sql = sql;
		this.redis = redis;
	}

	public void setSQLStructure(DatabaseStructure struct) {
		this.sql_db = struct;
	}

	public DatabaseStructure getSQLStructure() {
		return sql_db;
	}

	/**
	 * Relational: Entity Type contains only one attribute (id, name)
	 * Key-value: Key:EntityName Val:HashSet {<id, name>}
	 * 
	 * @param key
	 * @param entity
	 * @param data
	 *            ResultSet that only contains two columns [key, val] (1, Alice)
	 * @throws SQLException
	 * @author wangsha
	 * @date Oct 11, 2011
	 */
	public void toHashSet(String key, Schema entity, ResultSet data)
			throws SQLException {
		String hkey, hval;
		while (data.next()) {
			hkey = data.getString(1);
			hval = data.getString(2);
			redis.hset(key, hkey, hval);
		}
	}
	
	public void toValueHash(String keyPrefix, Schema entity, ResultSet data) throws SQLException {
		String pkey;
		while(data.next()) {
			pkey = keyPrefix+":"+data.getString(1);
			redis.hset(pkey, data.getString(2), data.getString(3));
		}
	}
	
	/**
	 * Relational: Entity with multiple attributes
	 * Key-value: create a HashSet
	 * for EACH entity e.g. key: student:1 val: {id: 1, name: Alice,
	 * NRIC:G1288943G, DoB: 25-01-1988}
	 * @param entity
	 * @param keySet
	 * @param dataSet 
	 * @throws SQLException
	 * @author wangsha
	 * @date Oct 11, 2011
	 */
	public void toHashSets(Schema entity, ResultSet dataSet) throws SQLException {
		String key;
		while (dataSet.next()) {
			String[] values = new String[entity.getAttributes().length];
			for(int i=0; i<entity.getAttributes().length; i++) {
				values[i] = dataSet.getString(i+1);
			}
			key = entity.getType() + Util.makeKeyString(values, entity.getKey().length);
			
			redis.hset(key, entity, values);
		}
	}
	
	/**
	 * Add single column resultset in to a set
	 * @param key
	 * @param dataSet
	 * @throws SQLException
	 * @author wangsha
	 * @date Oct 12, 2011
	 */
	public void toSet(String key, ResultSet dataSet) throws SQLException {
		while(dataSet.next()) {
			redis.sadd(key, dataSet.getString(1));
		}
	}


	/**
	 * Reverse Mapping of Entity with one attribute 
	 * (mainly to support query by that attribute i.e. SELECT id FROM student WHERE name = 'Alice')
	 * There may be multiple keys associated with a particular attribute
	 * Relational: Entity with one attribute
	 * Key-Value: A set for each attribute  student:name:Alice => set(1, 13, 243)
	 * @param entity
	 * @param data [key, attribute] (1, Alice)
	 * @throws SQLException
	 * @author wangsha
	 * @date Oct 11, 2011
	 */
	public void createReveserMappingSet(Schema entity, String attributeName, ResultSet data) 
			throws SQLException {
		
		// create forward mapping
		String key = entity.getType() + ":" + attributeName;
		String skey, sval;
		while (data.next()) {
			skey = data.getString(2); //attribute
			sval = data.getString(1); //original key
			
			redis.sadd(key+":"+skey, sval);
		}
		

	}
	


	
	/**
	 * @param args
	 * @author wangsha
	 * @date Oct 4, 2011
	 */
	public static void main(String[] args) {
		System.out.println("Done");

	}

}
