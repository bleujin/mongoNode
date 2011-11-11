package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ID;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.map.LRUMap;
import org.bson.types.ObjectId;

import com.mongodb.DB;

public class Repository {

	private DB db;
	private static ConcurrentHashMap<String, Repository> rss = new ConcurrentHashMap<String, Repository>(new LRUMap(5));
	private ReferenceManager rman ;

	private Repository(DB db) {
		this.db = db;
		rman = ReferenceManager.create(this) ;
	}

	static Repository create(DB db) {
		if (!rss.containsKey(db.getName())) {
			final Repository repository = new Repository(db);
			rss.put(db.getName(), repository);
			return repository ;
		} else {
			return rss.get(db.getName());
		}
	}

	Workspace getWorkspace(String wname) {
		return Workspace.create(this, db.getCollection(wname));
	}

	public Set<String> getWorkspaceNames() {
		return db.getCollectionNames();
	}

	DB getDB() {
		return db;
	}

	public ReferenceManager getReferenceManager() {
		return rman ;
	}

	Node findNodeById(String oid) {
		Set<String> cols = getWorkspaceNames() ;
		for (String colName : cols) {
			Node obj = getWorkspace(colName).findOne(PropertyQuery.createById(oid), Columns.ALL) ;
			if (obj != null) return obj ;
		}
		throw new IllegalArgumentException("id:" + oid + " not found");
	}

}
