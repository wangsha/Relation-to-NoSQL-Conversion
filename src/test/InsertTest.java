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
			if(Config.startRedisInEclipse) {
				pr = run.exec(cmd);
				pr.waitFor();
			}
			sql = new MySQLConnector(Config.host, Config.port, Config.db_user,
					Config.db_pwd, Config.db_name);
			redis = new Redis(Config.redis_server);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addStudent() throws SQLException {
		
		// SQL
		// Delete tuples if exist already
		String del = "DELETE FROM student WHERE nric='G1594325W'";
		int delCount = sql.update(del);
		del = "DELETE FROM person WHERE nric='G1594325W'";
		delCount += sql.update(del);
		if(debug_on)
			System.out.println("DELETE FROM student: "+delCount);
		
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
		
		Long delId = redis.del(key);
		System.out.println("The key deleted: "+delId);
		System.out.println("Student id: "+sid);
	}

	public void addMentor() throws SQLException {
		// SQL
		// Delete tuples if exist already
		String del = "DELETE FROM mentor WHERE stu_id=16 AND stf_id=5";
		int delCount = sql.update(del);
		if(debug_on)
			System.out.println("DELETE FROM menter: "+delCount);
		
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
//			test.addStudent();
			test.addMentor();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
