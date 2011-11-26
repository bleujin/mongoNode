package net.ion.radon.repository;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.myapi.ICredential;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class RepositoryCentral {

	private Mongo mongo;
	private String currentDBName = "test";
	private String user;
	private String pwd;

	public RepositoryCentral(String host, int port) throws UnknownHostException, MongoException{
		this(host, port, "test") ;
	}

	public RepositoryCentral(String host, int port, String defaultDBName) throws UnknownHostException, MongoException{
		this(host, port, defaultDBName, null, null);
	}
	
	public RepositoryCentral(String host, int port, String defaultDBName, String user, String pwd) throws UnknownHostException, MongoException{
		this.mongo = loadMongo(host, port) ;
		this.currentDBName = defaultDBName ;
		this.user = user;
		this.pwd = pwd;
	}	

	public RepositoryCentral(String url, String user, String pwd) throws UnknownHostException, MongoException{
		String[] urls = StringUtil.split(url, ":");
		
		this.mongo = loadMongo(urls[0], Integer.parseInt(urls[1])) ;
		this.currentDBName = urls[2];
		this.user = user;
		this.pwd = pwd;
	}
	
	private static Map<String, Mongo> STORE = MapUtil.newMap();
	private synchronized static Mongo loadMongo(String host, int port) throws UnknownHostException, MongoException {
		Mongo m = STORE.get(getKey(host, port)) ;
		if (m == null){
			m  = new Mongo(host, port) ;
			STORE.put(getKey(host, port), m) ;
		}
		return m;
	}

	public static RepositoryCentral testCreate() throws UnknownHostException, MongoException {
		return create("127.0.0.1", 27017);
	}

	public static RepositoryCentral testCreate(String dbName) throws UnknownHostException, MongoException {
		return create("127.0.0.1", 27017).changeDB(dbName);
	}
	
	public static RepositoryCentral create(String host, int port) throws UnknownHostException, MongoException {
		return new RepositoryCentral(host, port);
	}
	
	public static RepositoryCentral create(String host, int port, String dbName) throws UnknownHostException, MongoException {
		return new RepositoryCentral(host, port, dbName);
	}
	

	Mongo getMongo(){
		return mongo ;
	}
	
	private static String getKey(String host, int port) {
		return host + "/" + port;
	}

	public RepositoryCentral changeDB(String dbName){
		this.currentDBName = dbName ;
		return this ;
	}
	
	public Session testLogin(String wname) {
		return login(currentDBName, wname, new SimpleCredential()) ;
	}

	public Session login(String dbName, String defaultWorkspace, ICredential credential) {
		return LocalSession.create(RepositoryImpl.create(mongo.getDB(dbName)), defaultWorkspace);
	}
	
	public Session login(String dbName, String defaultWorkspace) {
		if(this.user == null) new Exception("User is null");
		if(this.pwd == null) new Exception("Password is null");

		DB db = mongo.getDB(dbName);
		if(!db.isAuthenticated()){
			boolean isLogin = db.authenticate(user, pwd.toCharArray());
			if(!isLogin) throw new IllegalArgumentException("Authenticate is false");	
		}

		final Repository repository = RepositoryImpl.create(db);
		return LocalSession.create(repository, defaultWorkspace);
	}
	
	public Session login(String defaultWorkspace) {
		return login(currentDBName, defaultWorkspace);
	}

	public void unload(){
		String key = "";
		for (Entry<String, Mongo> entry : STORE.entrySet() ) {
			if (mongo == entry.getValue()){
				key = entry.getKey() ;
				break ;
			}
		}
		STORE.remove(key) ;
		mongo.close() ;
	}
	
	public void shutDown() {
		try {
			Debug.warn("Request Mongo ShutDown....");
			mongo.getDB("admin").command(new BasicDBObject("shutdown", 1));
			Debug.warn("Mongo ShutDown....");
		} catch (MongoException ignore) {
			Debug.debug(ignore.getMessage());
		}
	}


}

class SimpleCredential implements ICredential {

	SimpleCredential() {
	}

	public String getUniqueId() {
		return "my test simple";
	}

}
