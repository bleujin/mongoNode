package net.ion.repository.mongo.index;

import net.ion.repository.mongo.Workspace;
import net.ion.repository.mongo.WriteSession;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class IndexHandler {

	private final WriteSession wsession;
	private DBObject options = new BasicDBObject() ;
	private DBObject columns = new BasicDBObject() ;
	
	private IndexHandler(WriteSession wsession, String indexName) {
		this.wsession = wsession ;
		options.put("name", indexName) ;
	}

	public static IndexHandler create(WriteSession wsession, String indexName) {
		return new IndexHandler(wsession, indexName);
	}

	public IndexHandler ascending(String propertyId) {
		columns.put(propertyId, 1) ;
		return this;
	}
	
	public IndexHandler descending(String propertyId) {
		columns.put(propertyId, -1) ;
		return this;
	}
	

	public IndexHandler unique(boolean unique) {
		options.put("unique", unique) ;
		return this;
	}

	public IndexHandler background(boolean background) {
		options.put("background", background) ;
		return this;
	}

	public void create() {
		wsession.addSessionJob(new SessionJob() {
			@Override
			public void run(Workspace workspace) {
				workspace.collection(wsession.readSession()).ensureIndex(columns, options);
			}
		}) ;
	}

	public void drop() {
		wsession.addSessionJob(new SessionJob() {
			@Override
			public void run(Workspace workspace) {
				workspace.collection(wsession.readSession()).dropIndex(options.get("name").toString());
			}
		}) ;
	}

}
