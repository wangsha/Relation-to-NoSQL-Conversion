/**
 * @file Util.java
 * @author wangsha
 * @date Oct 11, 2011 
 */
package script;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wangsha
 * 
 */
public class Util {
	public static String makeKeyString(String[] values, int size) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<size; i++) {
				sb.append(":"+values[i]);			
		}
		return sb.toString();

	}
}
