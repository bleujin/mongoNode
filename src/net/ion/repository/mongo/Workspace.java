package net.ion.repository.mongo;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import net.ion.repository.mongo.WriteSession.LogRow;
import net.ion.repository.mongo.exception.NotFoundPath;
import net.ion.repository.mongo.index.SessionJob;
import net.ion.repository.mongo.node.NodeResult;
import net.ion.repository.mongo.node.ReadChildren;
import net.ion.repository.mongo.node.ReadNode;
import net.ion.repository.mongo.node.WriteChildren;
import net.ion.repository.mongo.node.WriteNode;
import net.ion.repository.mongo.node.WriteNode.Touch;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class Workspace {

	private final RepositoryMongo repository;
	private final DB db;
	private TranExceptionHandler ehandler = TranExceptionHandler.PRINT;

	private Workspace(RepositoryMongo repository, DB db) {
		this.repository = repository;
		this.db = db;
	}

	static Workspace create(RepositoryMongo repository, DB db) {
		return new Workspace(repository, db);
	}

	
	public String name() {
		return db.getName();
	}

	
	public <T> T tran(ReadSession rsession, WriteJob<T> wjob) {
		WriteSession wsession = WriteSession.create(rsession);
		boolean isSuccess = true;
		try {
			wsession.beginTran();
			T result = wjob.handle(wsession);
			wsession.endTran();
			return result;
		} catch (Exception ex) {
			isSuccess = false;
			ehandler.handle(wsession, ex);
		} finally {
			wsession.completed(isSuccess);
		}
		return null;
	}

	public RepositoryMongo repository() {
		return repository;
	}

	public WriteNode pathBy(WriteSession wsession, Fqn fqn) {
		DBCollection col = db.getCollection(wsession.colName());
		BasicDBObject query = fqn.idQueryObject();
		DBObject found = col.findOne(query);
		if (found == null) {

			col.insert(query);
			found = query;
		}

		return WriteNode.create(wsession, fqn, found);
	}

	public ReadNode pathBy(ReadSession rsession, String fqn) {
		return pathBy(rsession, Fqn.fromString(fqn));
	}

	public ReadNode pathBy(ReadSession rsession, Fqn fqn) {

		BasicDBObject query = fqn.idQueryObject();
		DBObject found = collection(rsession).findOne(query);

		if (found == null) {
			if (! fqn.isRoot()) throw new NotFoundPath(fqn);

			BasicDBObject rootDBO = new BasicDBObject();
			rootDBO.put("_id", "/");
			rootDBO.put("_parent", "/");
			return ReadNode.create(rsession, fqn, rootDBO);
		} 
		return ReadNode.create(rsession, fqn, found);
	}

	public DBCollection collection(ReadSession rsession) {
		DBCollection col = db.getCollection(rsession.colName());
		return col;
	}

	public boolean exists(ReadSession rsession, String fqn) {
		return exists(rsession, Fqn.fromString(fqn));
	}

	public boolean exists(ReadSession rsession, Fqn fqn) {
		if (Fqn.ROOT.equals(fqn)) {
			return true;
		}
		return collection(rsession).getCount(fqn.idQueryObject()) > 0;
	}

	public boolean remove(Fqn target) {
		return true;
	}

	public boolean removeChildren(Fqn target) {
		return true;
	}

	
	public void dropCollection(ReadSession rsession) {
		db.getCollection(rsession.colName()).drop();
	}

	public void writeLog(WriteSession wsession, ReadSession rsession, List<SessionJob> sjobs, Set<LogRow> logRows) throws IOException {
		InstantLogWriter logWriter = new InstantLogWriter(this, wsession, rsession);
		logWriter.beginLog(logRows);
		for (LogRow row : logRows) {
			logWriter.writeLog(row);
		}
		logWriter.endLog();
		logRows.clear();
		
		for (SessionJob sjob : sjobs) {
			sjob.run(this);
		}
		
	}

	public ReadChildren children(ReadSession rsession, boolean includeSub, Fqn parent) {
		return new ReadChildren(rsession, includeSub, collection(rsession), parent);
	}

	public WriteChildren children(WriteSession wsession, boolean includeSub, Fqn parent) {
		return new WriteChildren(wsession, includeSub, collection(wsession.readSession()), parent);
	}

	static class InstantLogWriter {

		private final Workspace wspace;
		private final WriteSession wsession;
		private final ReadSession rsession;

		private DBCollection col;
		private WriteResult lastWriteResult;

		public InstantLogWriter(Workspace wspace, WriteSession wsession, ReadSession rsession) throws IOException {
			this.wspace = wspace;
			this.wsession = wsession;
			this.rsession = rsession;
		}

		public InstantLogWriter beginLog(Set<LogRow> logRows) throws IOException {
			final long thisTime = System.currentTimeMillis();
			this.col = wspace.db.getCollection(wsession.colName());

			return this;
		}

		public void writeLog(LogRow log) throws IOException {
			if (log.touch() == Touch.REMOVE) {
				col.remove(log.target().idQueryObject()) ;
			} else if (log.touch() == Touch.REMOVECHILDREN) {
				col.remove(log.target().childrenQueryObject()) ;
			} else {
				DBObject trow = log.source().found();
				trow.put("_lastmodified", GregorianCalendar.getInstance().getTimeInMillis());
				this.lastWriteResult = col.update(log.source().fqn().idQueryObject(), trow);
			}
		}

		public void endLog() throws IOException {
			rsession.attribute(NodeResult.class.getCanonicalName(), NodeResult.create(lastWriteResult));
		}

	}




}
