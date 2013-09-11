import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;

import org.apache.ibatis.io.Resources; 
import org.apache.ibatis.session.SqlSession; 
import org.apache.ibatis.session.SqlSessionFactory; 
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBTransfer{
	private Map<String,String> configs = new HashMap<String,String>();
	private String db_type;
	private String resource = "transfer-config.xml";
	private SqlSession session = null;

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
		if (this.session != null)
			return this.session;

		Properties props = new Properties();
		try{
			props.load(new FileInputStream(configs.get(name)));
			InputStream reader = Resources.getResourceAsStream(resource);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,props);
			this.session = sqlSessionFactory.openSession();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		return this.session;
	} 

	public User getUserByID(int id){
		SqlSession session= getSession();
		User user=null;
		user = session.selectOne("UserMapper.getUserByID",id);

		return user;
	}	
	
	public List<User> getAllUsers(){
		SqlSession session = getSession();
		List<User> users = null;
		users = session.selectList("UserMapper.getAllUsers");

		return users;
	}

	public List<User> getUserRange(int start,int end){
		SqlSession session = getSession();
		List<User> users = null;
		HashMap<String,Integer> range = new HashMap<String,Integer>();
		range.put("start",start);
		range.put("end",end);
		users = session.selectList("UserMapper.getUserRange",range);

		return users;
	}

	public void setUsers(List<User> users){
		SqlSession session = getSession();
		for(User user:users){
			session.insert("UserMapper.setUser",user);
			System.out.println(user.getId());
		}			
		User startUser = users.get(0);
		User endUser = users.get(users.size()-1);
		int startNo = startUser.getId();
		int endNo = endUser.getId();
		Log log = new Log();
		log.setStartNo(startNo);
		log.setEndNo(endNo);
		session.insert("LogMapper.setLog",log);
		session.commit();
	}

	public void setUser(User user){
		SqlSession session = getSession();
		session.insert("UserMapper.setUser",user);
		session.commit();
	}

	public void setLog(Log log){
		SqlSession session = getSession();
		session.insert("LogMapper.setLog",log);
		session.commit();
	}

	public int getLastNo(){
		SqlSession session = getSession();
		int result = 0;
		try {
			result = session.selectOne("LogMapper.getLog");
		}catch(Exception e){
			result =0;
		} 

		return result;	
	}

	public void close(){
		if (this.session != null)
			this.session.close();
	}

	public static void main(String[] argv) throws Exception{
		int COUNT = 10;
		DBTransfer source = new DBTransfer("source");
		DBTransfer target = new DBTransfer("target");

		int lastNo = target.getLastNo();	
		int startNo = lastNo+1;
		int endNo = startNo+COUNT;	
		List<User> users = source.getUserRange(startNo,endNo);

		for (User user: users){
			System.out.println(user.getId());	
		}	
		
		if (users.size()>0)
			target.setUsers(users);
		source.close();
		target.close();
	}
}
