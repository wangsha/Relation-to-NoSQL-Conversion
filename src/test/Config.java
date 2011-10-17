/**
 * @file Config.java
 * @author wangsha
 * @date Oct 4, 2011 
 */
package test;

/**
 * @author wangsha
 *
 */
public class Config {
	//SQL Connection
	public static String host = "localhost";
	public static int port = 8889;                    //tips: if you are using MAMP, port=8889
	public static String db_name = "cs4221";
	public static String db_user = "root";
	public static String db_pwd  = "root";
	
	//Redis Connection
	public static String redis_server = "localhost";
	public static String redis_cmd = "redis-server";
	
	//Other Settings
	public static boolean debug_on = false;
	public static boolean startRedisInEclipse = false;
}
