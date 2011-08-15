package net.ion.radon.repository;

import java.net.UnknownHostException;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.repository.myapi.ICredential;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class RepositoryCentral {

	private static Map<String, RepositoryCentral> STORE = MapUtil.newMap();
	private Mongo mongo;

	private RepositoryCentral(Mongo mongo) {
		this.mongo = mongo;
	}

	public static RepositoryCentral testLoad() throws UnknownHostException, MongoException {
		return load("61.250.201.78", 27017);
	}

	public static RepositoryCentral load(String host, int port) throws UnknownHostException, MongoException {
		RepositoryCentral rc = STORE.get(getKey(host, port));

		synchronized (RepositoryCentral.class) {
			if (rc == null) {
				final RepositoryCentral newRepo = new RepositoryCentral(new Mongo(host, port));
				STORE.put(getKey(host, port), newRepo) ;
				rc = newRepo ;
			}
		}

		return rc;
	}

	private static String getKey(String host, int port) {
		return host + "/" + port;
	}

	public Session testLogin(String dbName, String defaultWorkspace) {
		return login(dbName, defaultWorkspace, new SimpleCredential());
	}
	
	public Session testLogin(String wname) {
		return testLogin("test", wname) ;
	}

	public Session login(String dbName, String defaultWorkspace, ICredential credential) {
		final Repository repository = Repository.create(mongo.getDB(dbName));
		return Session.create(repository, defaultWorkspace);
	}

	public void shutDown() {
		try {
			Debug.warn("Request Mongo ShutDown....");
			new Mongo().getDB("admin").command(new BasicDBObject("shutdown", 1));
			Debug.warn("Mongo ShutDown....");
		} catch (MongoException ignore) {
			Debug.debug(ignore.getMessage());
		} catch (UnknownHostException ignore) {
			Debug.debug(ignore.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			RepositoryCentral.testLoad().shutDown();
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
