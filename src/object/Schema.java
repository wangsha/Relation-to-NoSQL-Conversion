/**
 * @file Entity.java
 * @author wangsha
 * @date Oct 11, 2011 
 */
package object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author wangsha
 *
 */
public class Schema {
	private String type;
	String[] attributes;
	String[] key;
	
	public Schema(String type, String[] attributes, String[] key) {
		this.type = type;
		this.attributes = attributes;
		this.key = key;
	}
	
	public String getType() {
		return type;
	}
	
	public String[] getKey() {
		return key;
	}
	public String[] getAttributes() {
		return attributes;
	}
}
