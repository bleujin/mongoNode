package net.ion.radon.repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.map.LRUMap;

import com.mongodb.DB;

public class RepositoryImpl implements Repository {

	private DB db;
	private static ConcurrentHashMap<String, Repository> rss = new ConcurrentHashMap<String, Repository>(new LRUMap(5));

	private RepositoryImpl(DB db) {
		this.db = db;
	}

	synchronized static Repository create(DB db) {
		if (!rss.containsKey(db.getName())) {
			final Repository repository = new RepositoryImpl(db);
			rss.put(db.getName(), repository);
		}
		return rss.get(db.getName());
	}

	public Workspace getWorkspace(String wname, WorkspaceOption option) {
		if (! getDB().collectionExists(wname)){
			getDB().createCollection(wname, option.getDBObject()) ;
		}
		return Workspace.load(getDB().getCollection(wname.toLowerCase()));
	}

	public Set<String> getWorkspaceNames() {
		return getDB().getCollectionNames();
	}

	private DB getDB() {
		return db;
	}
	
	public String toString(){
		return db.getName() + "[" + getClass().getCanonicalName() + "]" ;
	}
}
