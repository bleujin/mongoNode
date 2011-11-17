package net.ion.radon.repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.map.LRUMap;

import com.mongodb.DB;

public class Repository {

	private DB db;
	private static ConcurrentHashMap<String, Repository> rss = new ConcurrentHashMap<String, Repository>(new LRUMap(5));

	private Repository(DB db) {
		this.db = db;
	}

	synchronized static Repository create(DB db) {
		if (!rss.containsKey(db.getName())) {
			final Repository repository = new Repository(db);
			rss.put(db.getName(), repository);
		}
		return rss.get(db.getName());
	}

	Workspace getWorkspace(String wname) {
		return Workspace.load(db.getCollection(wname.toLowerCase()));
	}

	public Set<String> getWorkspaceNames() {
		return db.getCollectionNames();
	}

	DB getDB() {
		return db;
	}
}
