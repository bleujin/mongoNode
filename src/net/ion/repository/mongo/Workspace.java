package net.ion.repository.mongo;

import java.awt.image.RescaleOp;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.stream.JsonWriter;
import net.ion.framework.parse.html.NotFoundTagException;
import net.ion.framework.util.CalendarUtils;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.ListUtil;
import net.ion.repository.mongo.WriteSession.LogRow;
import net.ion.repository.mongo.exception.NotFoundPath;
import net.ion.repository.mongo.node.ReadChildren;
import net.ion.repository.mongo.node.ReadNode;
import net.ion.repository.mongo.node.WriteNode;
import net.ion.repository.mongo.node.WriteNode.Touch;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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

	public <T> T tran(ReadSession rsession, WriteJob<T> wjob) {
		WriteSession wsession = WriteSession.create(rsession);
		boolean isSuccess = true ;
		try {
			wsession.beginTran();
			T result = wjob.handle(wsession);
			wsession.endTran();
			return result;
		} catch (Exception ex) {
			isSuccess = false ;
			ehandler.handle(wsession, ex);
		} finally {
			wsession.completed(isSuccess) ;
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
		return pathBy(rsession, Fqn.fromString(fqn)) ;
	}
	
	public ReadNode pathBy(ReadSession rsession, Fqn fqn) {
		DBCollection col = collection(rsession);
		BasicDBObject query = fqn.idQueryObject();
		DBObject found = col.findOne(query);

		if (found == null) throw new NotFoundPath(fqn) ;
		return ReadNode.create(rsession, fqn, found);
	}

	private DBCollection collection(ReadSession rsession) {
		DBCollection col = db.getCollection(rsession.colName());
		return col;
	}

	public boolean exists(ReadSession rsession, String fqn){
		return exists(rsession, Fqn.fromString(fqn)) ;
	}
	
	public boolean exists(ReadSession rsession, Fqn fqn) {
		if (Fqn.ROOT.equals(fqn)) {
			return true;
		}
		return collection(rsession).getCount(fqn.idQueryObject()) > 0 ;
	}
	
	
	public void dropCollection(ReadSession rsession) {
		db.getCollection(rsession.colName()).drop();
	}

	public void writeLog(WriteSession wsession, ReadSession rsession, Set<LogRow> logRows) throws IOException {
		InstantLogWriter logWriter = new InstantLogWriter(this, wsession, rsession) ;
		logWriter.beginLog(logRows);
		for (LogRow row : logRows) {
			logWriter.writeLog(row);
		}
		logWriter.endLog();
		logRows.clear();
	}
	
	public ReadChildren children(ReadSession rsession, Fqn parent) {
		return new ReadChildren(rsession, collection(rsession), parent) ;
	}


	static class InstantLogWriter {

		private final Workspace wspace;
		private final WriteSession wsession;
		private final ReadSession rsession;

		private DBCollection col;

		public InstantLogWriter(Workspace wspace, WriteSession wsession, ReadSession rsession) throws IOException {
			this.wspace = wspace;
			this.wsession = wsession;
			this.rsession = rsession;
		}

		public InstantLogWriter beginLog(Set<LogRow> logRows) throws IOException {
			final long thisTime = System.currentTimeMillis();
			this.col = wspace.db.getCollection(wsession.colName()) ;

			return this;
		}

		public void writeLog(LogRow log) throws IOException {
			DBObject trow = log.source().dbObject();
			trow.put("_lastmodified", GregorianCalendar.getInstance().getTimeInMillis()) ;
			col.update(log.source().fqn().idQueryObject(), trow) ;
		}

		public void endLog() throws IOException {
		}

	}


}
