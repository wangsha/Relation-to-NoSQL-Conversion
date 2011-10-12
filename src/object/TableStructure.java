/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import database.MySQLConnector;

/**
 * 
 * @author liyuchen
 */
public class TableStructure {
	String tableName;

	/**
	 * Map<attribute name, domain>
	 */
	Map<String, String> attributes;
	ArrayList<String> key;

	public TableStructure(String name) {
		tableName = name;
		attributes = new HashMap<String, String>();
		key = new ArrayList<String>();
	}

	public void getStructure(MySQLConnector sql) {
		ResultSet rs;
		try {
			rs = sql.query("DESCRIBE " + tableName);
			while (rs.next()) {
				attributes.put(rs.getString(1), rs.getString(2).split("\\(")[0]);
				if (rs.getString(4).equals("PRI"))
					key.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	

	public String toString() {
		StringBuilder sb = new StringBuilder("Table name: " + tableName + "\n");
		Iterator<String> it = attributes.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			sb.append(key);
			sb.append(" [" + attributes.get(key) + "]\n");
		}

		sb.append("\n Keys: \n");

		it = key.iterator();
		while (it.hasNext()) {
			sb.append(it.next() + " ");
		}
		sb.append("\n");
		return sb.toString();

	}

}
