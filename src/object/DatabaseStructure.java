/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package object;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import database.MySQLConnector;

import script.Converter;

/**
 * 
 * @author liyuchen
 */
public class DatabaseStructure {
	ArrayList<TableStructure> tables;

	public DatabaseStructure() {
		tables = new ArrayList<TableStructure>();
	}
	
	public TableStructure getTable(String tableName) {
		Iterator<TableStructure> it = tables.iterator();
		while(it.hasNext()) {
			TableStructure t = it.next();
			if(t.tableName == tableName) {
				return t;
			}
		}
		return null;
	}

	public void getStructure(MySQLConnector sql) {
		try {

			ResultSet rs = sql.query("SHOW TABLES");
			while (rs.next()) {
				tables.add(new TableStructure(rs.getString(1)));
			}
			for (int i = 0; i < tables.size(); i++) {
				tables.get(i).getStructure(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String toString() {
		String properties = "";
		for (int i = 0; i < tables.size(); i++) {
			properties += tables.get(i).toString();
		}
		return properties;
	}

}
