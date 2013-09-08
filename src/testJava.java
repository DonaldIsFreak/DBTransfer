import java.sql.*;

import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class testJava{
	public static void main(String[] argv) throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		String resource = "test-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession session = sqlSessionFactory.openSession();
		try{
			User user = session.selectOne("UserMapper.selectUser",1);
			System.out.println("User Id:"+user.getId());
			System.out.println("User Name:"+user.getName());
		}finally{
			session.close();
		}
	}
}
