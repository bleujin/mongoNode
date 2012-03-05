package net.ion.radon.repository;

import java.awt.geom.CubicCurve2D;
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
import com.mongodb.MongoURI;
import com.mongodb.WriteConcern;

public class RepositoryCentral {

	private Mongo mongo;
	private String currentDBName = "test";
	private String userId;
	private String pwd;

	public RepositoryCentral(String host, int port) throws UnknownHostException, MongoException {
		this(host, port, "test");
	}

	public RepositoryCentral(String host, int port, String defaultDBName) throws UnknownHostException, MongoException {
		this(host, port, defaultDBName, null, null);
	}

	public RepositoryCentral(String host, int port, String defaultDBName, String userId, String pwd) throws UnknownHostException, MongoException {
		this(loadMongo(host, port), defaultDBName, userId, pwd);
	}

	private RepositoryCentral(Mongo mongo, String dbName, String userId, String pwd) {
		this.mongo = mongo;
		this.currentDBName = dbName;
		this.userId = userId;
		this.pwd = pwd;
	}

	// Replica-Set
	public final static RepositoryCentral createReliica(String uri, String dbName, String userId, String pwd) throws MongoException, UnknownHostException {
		Mongo mongo = new Mongo(new MongoURI(uri));
		mongo.setWriteConcern(WriteConcern.REPLICAS_SAFE);
		return new RepositoryCentral(mongo, dbName, userId, pwd) ;
	}

	public RepositoryCentral(String url) throws UnknownHostException, MongoException {
		this(url, null, null);
	}

	public RepositoryCentral(String url, String user, String pwd) throws UnknownHostException, MongoException {
		String[] urls = StringUtil.split(url, ":");

		this.mongo = loadMongo(urls[0], Integer.parseInt(urls[1]));
		this.currentDBName = urls[2];
		this.userId = user;
		this.pwd = pwd;
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

	public static RepositoryCentral testCreate() throws UnknownHostException, MongoException {
		return testCreate("test");
	}

	public static RepositoryCentral testCreate(String dbName) throws UnknownHostException, MongoException {
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
		return login(currentDBName, wname, new SimpleCredential());
	}

	public Session login(String dbName, String defaultWorkspace, ICredential credential) {
		return LocalSession.create(LocalRepository.create(mongo.getDB(dbName)), defaultWorkspace);
	}

	public Session login(String dbName, String defaultWorkspace) throws IllegalArgumentException {

		DB db = mongo.getDB(dbName);

		if (!db.isAuthenticated() && this.userId != null && this.pwd != null) {
			boolean isLogin = db.authenticate(userId, pwd.toCharArray());
			if (!isLogin)
				throw new IllegalArgumentException("Authenticate is false");
		}

		final Repository repository = LocalRepository.create(db);
		return LocalSession.create(repository, defaultWorkspace);
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

}

class SimpleCredential implements ICredential {

	SimpleCredential() {
	}

	public String getUniqueId() {
		return "my test simple";
	}

}
