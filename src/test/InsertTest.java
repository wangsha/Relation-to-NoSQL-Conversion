/**
 * @file QueryTest.java
 * @author wangsha
 * @date Oct 12, 2011 
 */
package test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.MySQLConnector;
import database.Redis;

/**
 * @author wangsha
 * 
 */
public class InsertTest {
	private MySQLConnector sql;
	private Redis redis;
	private boolean debug_on = true;

	private String[] stu_attributes = new String[] { "id", "nric", "name",
			"dob", "matric_no", "dept_id" };

	public InsertTest() {
		String cmd = "redis-server";
		Runtime run = Runtime.getRuntime();
		Process pr;
		try {
			pr = run.exec(cmd);
			pr.waitFor();
			sql = new MySQLConnector("127.0.0.1", 3306, Config.db_user,
					Config.db_pwd, Config.db_name);
			redis = new Redis(Config.redis_server);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addStudent() throws SQLException {
		// SQL
		// Insert Person
		PreparedStatement query = sql
				.prepare(
						"INSERT INTO `person` VALUES('G1594325W', 'Alice Wong', '1989-09-12 00:00:00')",
						null);
		query.execute();

		// Insert Student
		query = sql
				.prepare(
						"INSERT INTO `student` (nric, dept_id, matric_no) VALUES('G1594325W', 1, 'U0761235A')",
						null);
		query.execute();
		ResultSet data = sql
				.query("SELECT id FROM student WHERE nric='G1594325W'");
		data.next();
		String pid = data.getString(1);
		System.out.println(pid);

		// Redis
		// Get next student id
		Long sid = redis.incr("studentid");
		System.out.println(sid);
		String key = "student:" + sid;

		// Insert Student
		redis.hsetall(key, stu_attributes, new String[] { sid.toString(),
				"G1594325W", "Alice Wong", "1989-09-12 00:00:00", "U0761235A",
				"1" });

		if (debug_on)
			System.out.println(redis.hgetAll(key));

	}

	public void addMentor() throws SQLException {
		// SQL
		// Insert mentor
		PreparedStatement query = sql
				.prepare(
						"INSERT INTO `mentor` VALUES(16, 5)",
						null);
		query.execute();
		
		
		// Redis
		redis.hset("mentor", "16", "5");
		// Reverse mapping
		redis.sadd("mentor:stf_id:5", "16");
	}

	/**
	 * @param args
	 * @author wangsha
	 * @date Oct 12, 2011
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InsertTest test = new InsertTest();
		try {
			//test.addStudent();
			test.addMentor();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
