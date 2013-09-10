import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

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
	
	public List<User> getAllUsers(){
		SqlSession session = getSession();
		List<User> users = null;
		try {
			users = session.selectList("UserMapper.getAllUsers");
		}finally {
			session.close();
		}

		return users;
	}

	public void setUsers(List<User> users){
		SqlSession session = getSession();
		try {
			for(User user:users){
				session.insert("UserMapper.setUser",user);
			}			
			session.commit();
		} finally {
			session.close();
		}
	}
	public void setUser(User user){
		SqlSession session = getSession();
		try{
			session.insert("UserMapper.setUser",user);
			session.commit();
		}finally {
			session.close();
		}
	}
	public void setLog(Log log){
		SqlSession session = getSession();
		try {
			session.insert("LogMapper.setLog",log);
			session.commit();
		}finally {
			session.close();
		}
	}
	public int getLastNo(){
		SqlSession session = getSession();
		int result = 0;
		try {
			result = session.selectOne("LogMapper.getLog");
		} finally {
			session.close();
		}

		return result;	
	}

	public static void main(String[] argv) throws Exception{
		DBTransfer source = new DBTransfer("source");
		DBTransfer target = new DBTransfer("target");
		User user = source.getUserByID(1);	
		System.out.println(user);
		List<User> users = source.getAllUsers();
		target.setUsers(users);
		Log log = new Log();
		log.setStartNo(1);
		log.setEndNo(5);
		System.out.println(log);
		target.setLog(log);
		System.out.println(target.getLastNo());
	}
}
