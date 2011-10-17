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

	private String[] stu_attributes = new String[] { "id", "nric", "name",
			"dob", "matric_no", "dept_id" };

	public InsertTest() {
		Runtime run = Runtime.getRuntime();
		Process pr;
		try {
			if(Config.startRedisInEclipse) {
				pr = run.exec(Config.redis_cmd);
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
		if(Config.debug_on)
			System.out.println("DELETE FROM student: "+delCount);
		
		// SQL
		// Insert Person
		PreparedStatement query = sql
				.prepare(
						"INSERT INTO `person` VALUES('G1594325W', 'Alice Wong', '1989-09-12 00:00:00')",
						null);
		query.execute();
		
		//Output Comment
		System.out.println("==============Insert Into person Table==============");
		System.out.println("|| G1594325W || Alice Wong || 1989-09-12 00:00:00 ||");
		
		// Insert Student
		query = sql
				.prepare(
						"INSERT INTO `student` (nric, dept_id, matric_no) VALUES('G1594325W', 1, 'U0761235A')",
						null);
		query.execute();
		
		//Output Comment
				System.out.println("==============Insert Into student Table==============");
				System.out.println("||    G1594325W  ||    1    ||      U0761235A      ||");
				System.out.println("=====================================================");
		
		//Query
		ResultSet data = sql
				.query("SELECT id FROM student WHERE nric='G1594325W'");
		data.next();
		String pid = data.getString(1);		
		System.out.println("SQL Query: SELECT id FROM student WHERE nric='G1594325W'");
		System.out.println("Result:    id = "+ pid);
		data = sql
				.query("SELECT dept_id FROM student WHERE nric='G1594325W'");
		data.next();
		String did = data.getString(1);
		System.out.println("\nSQL Query: SELECT dept_id FROM student WHERE nric='G1594325W'");
		System.out.println("Result:    dept_id = "+ did);
		data = sql
				.query("SELECT matric_no FROM student WHERE nric='G1594325W'");
		data.next();
		String matric = data.getString(1);
		System.out.println("\nSQL Query: SELECT matric_no FROM student WHERE nric='G1594325W'");
		System.out.println("Result:    matric_no = "+ matric);
		
		System.out.println("=====================================================");
		// Redis
		// Get next student id
		Long sid = redis.incr("studentid");
		if(Config.debug_on)
			System.out.println(sid);
		String key = "student:" + sid;

		// Insert Student
		redis.hsetall(key, stu_attributes, new String[] { sid.toString(),
				"G1594325W", "Alice Wong", "1989-09-12 00:00:00", "U0761235A",
				"1" });

		//Output comment
		System.out.println("\n==============Redis hsetall a new student==============");
		System.out.println(sid.toString() + ",G1594325W,Alice Wong,1989-09-12 00:00:00,U0761235A,1");
		System.out.println("=========================================================");
		System.out.println("redis command: redis.hgetAll(key)");
		System.out.println("result:        "+redis.hgetAll(key));
		
		Long delId = redis.del(key);
		if (Config.debug_on)
			System.out.println("The key deleted: "+delId);
	}

	public void addMentor() throws SQLException {
		// SQL
		// Delete tuples if exist already
		String del = "DELETE FROM mentor WHERE stu_id=16 AND stf_id=5";
		int delCount = sql.update(del);
		if(Config.debug_on)
			System.out.println("DELETE FROM menter: "+delCount);
		
		// SQL
		// Insert mentor
		PreparedStatement query = sql
				.prepare(
						"INSERT INTO `mentor` VALUES(16, 5)",
						null);
		query.execute();
		
		//Output Comment
		System.out.println("\n\n==============Insert Into menter Table==============");
		System.out.println("||    16               ||            5            ||");
		System.out.println("====================================================");
		
		//Query
				ResultSet data = sql
						.query("SELECT stf_id FROM mentor WHERE stu_id=16");
				data.next();
				String stf_id = data.getString(1);		
				System.out.println("SQL Query: SELECT stf_id FROM mentor WHERE stu_id=16");
				System.out.println("Result:    id = "+ stf_id);
		
		// Redis
		redis.hset("mentor", "16", "5");
		// Reverse mapping
		redis.sadd("mentor:stf_id:5", "16");
		
		//Output comment
				System.out.println("\n==============Redis hset a mentorship==============");
				System.out.println("mentor,16,5");
				System.out.println("============Redis sadd a reverse mapping==============");
				System.out.println("mentor:stf_id:5,16");
				System.out.println("======================================================");

				System.out.println("redis command: redis.hget('mentor','16')");
				System.out.println("result:        "+redis.hget("mentor", "16"));
				
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
			test.addStudent();
			test.addMentor();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
