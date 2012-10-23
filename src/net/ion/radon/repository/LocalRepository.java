package net.ion.radon.repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.map.LRUMap;

import com.mongodb.DB;

public class LocalRepository implements Repository {

	private DB db;
	private static ConcurrentHashMap<String, Repository> rss = new ConcurrentHashMap<String, Repository>(new LRUMap(15));

	private LocalRepository(DB db) {
		this.db = db;
	}

	synchronized static Repository create(DB db) {
		if (!rss.containsKey(db.getName())) {
			final Repository repository = new LocalRepository(db);
			rss.put(db.getName(), repository);
		}
		return rss.get(db.getName());
	}

	public Workspace getWorkspace(String wname, WorkspaceOption option) {
		return Workspace.load(getDB(), wname, option);
	}

	public Set<String> getWorkspaceNames() {
		return getDB().getCollectionNames();
	}

	private DB getDB() {
		return db;
	}

	public String toString() {
		return db.getName() + "[" + getClass().getCanonicalName() + "]";
	}
}
