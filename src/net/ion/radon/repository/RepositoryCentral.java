package net.ion.radon.repository;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
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
		this.mongo = loadMongo(host, port) ;
		this.currentDBName = defaultDBName ;
	}
	
	public RepositoryCentral(String host, int port, String defaultDBName, String user, String pwd) throws UnknownHostException, MongoException{
		this.mongo = loadMongo(host, port) ;
		this.currentDBName = defaultDBName ;
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
	

	Mongo getMongo(){
		return mongo ;
	}
	
	private static String getKey(String host, int port) {
		return host + "/" + port;
	}

	@Deprecated
	public Session testLogin(String dbName, String defaultWorkspace) {
		this.currentDBName = dbName ;
		return login(dbName, defaultWorkspace, new SimpleCredential());
	}
	
	public RepositoryCentral changeDB(String dbName){
		this.currentDBName = dbName ;
		return this ;
	}
	
	public Session testLogin(String wname) {
		return testLogin(currentDBName, wname) ;
	}

	public Session login(String dbName, String defaultWorkspace, ICredential credential) {
		final Repository repository = Repository.create(mongo.getDB(dbName));
		return LocalSession.create(repository, defaultWorkspace);
	}
	
	public Session login(String dbName, String defaultWorkspace) {
		if(this.user == null) new Exception("User is null");
		if(this.pwd == null) new Exception("Password is null");

		DB db = mongo.getDB(dbName);
		if(!db.isAuthenticated()){
			boolean isLogin = db.authenticate(user, pwd.toCharArray());
			if(!isLogin) new Exception("Authenticate is false");	
		}

		final Repository repository = Repository.create(db);
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

	public static void main(String[] args) {
		try {
			// RepositoryCentral.testLoad().shutDown();
		} catch (Exception ex) {
			ex.printStackTrace();
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
