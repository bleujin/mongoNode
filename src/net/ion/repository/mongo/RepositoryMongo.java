package net.ion.repository.mongo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import net.ion.repository.ICredential;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class RepositoryMongo {

	private ICredential credential = SimpleCredential.BLANK;
	private final Mongo mongo;
	private Cache<String, Workspace> wss = CacheBuilder.newBuilder().build();

	private RepositoryMongo(Mongo mongo) {
		this.mongo = mongo;
	}

	public static RepositoryMongo testLocal() throws IOException {
		return create(SimpleCredential.BLANK, InetAddress.getLocalHost(), 27017);
	}

	public static RepositoryMongo test(String address, int port) throws IOException {
		return create(SimpleCredential.BLANK, InetAddress.getByName(address), 27017);
	}
	
	public static RepositoryMongo testLocalWithShutdownHook() throws IOException {
		final RepositoryMongo result = create(SimpleCredential.BLANK, InetAddress.getLocalHost(), 27017);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				result.shutdown(); 
			}
		});
		
		return result;
	}

	
	private static RepositoryMongo create(ICredential credential, InetAddress saddress, int port) throws IOException {
		Mongo mongo = new Mongo(saddress.getHostAddress(), port);

		RepositoryMongo result = new RepositoryMongo(mongo);
		if (credential.isAuthenticated(result))
			return result;
		throw new IllegalArgumentException("not authenticated");
	}

	public ReadSession login(final String dbName, final String colName) throws IOException {
		try {
			Workspace ws = wss.get(dbName, new Callable<Workspace>() {
				public Workspace call() throws Exception {
					DB db = mongo.getDB(dbName);
					return Workspace.create(RepositoryMongo.this, db, new WithInExecutorService());
				}
			});
			return ReadSession.create(ws, Credential.ADMIN, colName);
		} catch (ExecutionException ex) {
			throw new IOException(ex.getMessage()) ;
		}
	}

	public void shutdown() {
		mongo.close(); 
	}

}

class SimpleCredential implements ICredential {
	public static SimpleCredential BLANK = new SimpleCredential();

	private SimpleCredential() {
	}

	public String getUniqueId() {
		return "my test simple";
	}

	public boolean isBlank() {
		return true;
	}

	public boolean isAuthenticated(RepositoryMongo rm) {
		return true;
	}
}