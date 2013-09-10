import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.util.List;
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
	private String db_type;
	private String resource = "transfer-config.xml";

	public DBTransfer(String db_type){
		// Create a Dict to store file name.
		configs.put("source","source_config.properties");
		configs.put("target","target_config.properties");
		this.db_type = db_type;
	}

	public SqlSession getSession(){
		return getSession(this.db_type);
	}	

	public SqlSession getSession(String name){
		Properties props = new Properties();
		try{
			props.load(new FileInputStream(configs.get(name)));
			InputStream reader = Resources.getResourceAsStream(resource);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,props);
			return sqlSessionFactory.openSession();
		}catch(IOException e){
		}
		return null;
	} 
	public User getUserByID(int id){
		SqlSession session= getSession();
		User user=null;
		try{
			user = session.selectOne("UserMapper.getUserByID",id);
		}finally {
			session.close();
		}
		return user;
	}	

	public static void main(String[] argv) throws Exception{
		DBTransfer transfer = new DBTransfer("source");
		User user = transfer.getUserByID(1);	
		System.out.println(user);
	}
}
