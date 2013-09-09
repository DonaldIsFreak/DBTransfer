import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import org.apache.ibatis.io.Resources; 
import org.apache.ibatis.session.SqlSession; 
import org.apache.ibatis.session.SqlSessionFactory; 
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBTransfer{
	private Map<String,String> configs = new HashMap<String,String>();
	private String resource = "transfer-config.xml";

	public DBTransfer(){
		// Create a Dict to store file name.
		configs.put("source","source_config.properties");
		configs.put("target","target_config.properties");
	}
	
	public SqlSession getSession(String name) throws IOException{
		Properties props = new Properties();
		props.load(new FileInputStream(configs.get(name)));
		InputStream reader = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,props);
		return sqlSessionFactory.openSession();
	} 

	public static void main(String[] argv) throws Exception{
		DBTransfer transfer = new DBTransfer();
		SqlSession session = transfer.getSession("source");
		try{
			User user = session.selectOne("UserMapper.selectUser",1);
			System.out.println(user.getId());
		}finally{
			session.close();
		}
	}
}
