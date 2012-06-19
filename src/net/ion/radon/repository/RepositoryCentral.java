package net.ion.radon.repository;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.admin.DBStatus;
import net.ion.radon.repository.myapi.ICredential;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class RepositoryCentral implements RCentral {

	private Mongo mongo;
	private String currentDBName = "test";
	private ICredential credential = SimpleCredential.BLANK;

	public RepositoryCentral(String host, int port) throws UnknownHostException, MongoException {
		this(host, port, "test");
	}

	public RepositoryCentral(String host, int port, String defaultDBName) throws UnknownHostException, MongoException {
		this(host, port, defaultDBName, null, null);
	}

	public RepositoryCentral(String host, int port, String defaultDBName, String userId, String pwd) throws UnknownHostException, MongoException {
		this(loadMongo(host, port), defaultDBName, userId, pwd);
	}

	protected RepositoryCentral(Mongo mongo, String dbName, String userId, String pwd) {
		this.mongo = mongo;
		this.currentDBName = dbName;
		this.credential = BasicCredential.create(userId, pwd) ;
	}

	// Replica-Set
	// use -> public static RepositoryCentral create(Mongo mongo, String dbName, String userId, String pwd) {
//	public final static RepositoryCentral createReliica(String uri, String dbName, String userId, String pwd) throws MongoException, UnknownHostException {
//		Mongo mongo = new Mongo(new MongoURI(uri));
//		mongo.setWriteConcern(WriteConcern.REPLICAS_SAFE);
//		return new RepositoryCentral(mongo, dbName, userId, pwd) ;
//	}

	public RepositoryCentral(String url) throws UnknownHostException, MongoException {
		this(url, null, null);
	}

	public RepositoryCentral(String url, String userId, String pwd) throws UnknownHostException, MongoException {
		String[] urls = StringUtil.split(url, ":");

		this.mongo = loadMongo(urls[0], Integer.parseInt(urls[1]));
		this.currentDBName = urls[2];
		this.credential = BasicCredential.create(userId, pwd) ;
	}

	private static Map<String, Mongo> STORE = MapUtil.newMap();

	private synchronized static Mongo loadMongo(String host, int port) throws UnknownHostException, MongoException {
		Mongo m = STORE.get(getKey(host, port));
		if (m == null) {
			m = new Mongo(host, port);
			STORE.put(getKey(host, port), m);
		}
		return m;
	}

	public static RepositoryCentral testCreate() throws UnknownHostException, IOException {
		return testCreate("test");
	}

	public static RepositoryCentral testCreate(String dbName) throws UnknownHostException, IOException {
		return create("61.250.201.78", 27017).changeDB(dbName);
	}

	public static RepositoryCentral create(String host, int port) throws UnknownHostException, MongoException {
		return new RepositoryCentral(host, port);
	}

	public static RepositoryCentral create(String host, int port, String dbName) throws UnknownHostException, MongoException {
		return new RepositoryCentral(host, port, dbName);
	}

	public static RepositoryCentral create(Mongo mongo, String dbName, String userId, String pwd) {
		String key = mongo.getServerAddressList().toString();
		STORE.put(mongo.getServerAddressList().toString(), mongo);
		return new RepositoryCentral(mongo, dbName, userId, pwd);
	}

	Mongo getMongo() {
		return mongo;
	}

	private static String getKey(String host, int port) {
		return host + "/" + port;
	}

	public RepositoryCentral changeDB(String dbName) {
		this.currentDBName = dbName;
		return this;
	}

	public Session testLogin(String wname) {
		return login(currentDBName, wname);
	}
	public Session login(String dbName, String defaultWorkspace) throws IllegalArgumentException {

		DB db = mongo.getDB(dbName);

		if (!credential.isAuthenticated(db)) throw new IllegalArgumentException("Authenticate is false");

		return LocalSession.create(LocalRepository.create(db), defaultWorkspace);
	}

	public Session login(String defaultWorkspace) throws IllegalArgumentException {
		return login(currentDBName, defaultWorkspace);
	}

	public void unload() {
		String key = "";
		for (Entry<String, Mongo> entry : STORE.entrySet()) {
			if (mongo == entry.getValue()) {
				key = entry.getKey();
				break;
			}
		}
		STORE.remove(key);
		mongo.close();
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

	public DBStatus getDBStatus(){
		DB db = mongo.getDB(currentDBName) ;
		return DBStatus.create(mongo.getDB("system"), db) ;
	}
	
}

class SimpleCredential implements ICredential {

	static SimpleCredential BLANK = new SimpleCredential() ;
	
	private SimpleCredential() {
	}

	public String getUniqueId() {
		return "my test simple";
	}

	public boolean isBlank() {
		return true;
	}

	public boolean isAuthenticated(DB db) {
		return true;
	}
}

class BasicCredential implements ICredential {

	private String userId ;
	private String pwd ;
	private BasicCredential(String userId, String pwd) {
		this.userId = userId ;
		this.pwd = pwd ;
	}

	final static ICredential create(String userId, String pwd){
		if (StringUtil.isBlank(userId)) return SimpleCredential.BLANK ;
		return new BasicCredential(userId, pwd) ;
	}
	
	public String getUniqueId() {
		return userId;
	}

	public boolean isBlank() {
		return false;
	}

	public boolean isAuthenticated(DB db) {
		return (!db.isAuthenticated()) || db.authenticate(userId, pwd.toCharArray());
	}
	
}
