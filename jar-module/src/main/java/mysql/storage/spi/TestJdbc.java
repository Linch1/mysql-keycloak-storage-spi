package mysql.storage.spi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

public class TestJdbc {
	public static void main( String[] args) throws SQLException {
		String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/glugluwinw?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull";
		String user = "mc27";
		String passw = "mc27";
		
		System.out.println("connecting ...");
		Connection myConn = DriverManager.getConnection(jdbcUrl, user, passw);
		System.out.println("connected");


	}
}
