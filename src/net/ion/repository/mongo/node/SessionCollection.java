package net.ion.repository.mongo.node;

import org.apache.ecs.xhtml.s;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.mr.RowJob;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;

public class SessionCollection {

	private ReadSession session;
	SessionCollection(ReadSession session) {
		this.session = session ;
	}

	public static SessionCollection create(ReadSession session) {
		return new SessionCollection(session);
	}

	public long count(){
		return collection().count() ;
	}
	
	public void debugPrint() {
		DBCursor cursor = collection().find() ;
		try {
			while(cursor.hasNext()){
				Debug.println(cursor.next()) ;
			}
		} finally {
			cursor.close(); 
		}
	}

	private DBCollection collection() {
		return session.workspace().collection(session);
	}
	
	
	public void drop() {
		collection().drop();;
	}


	<T> T mapReduce(MapReduce mapReduce, ReadChildren rchildren, RowJob<T> rowJob) {
		DBCollection collection = collection() ;
		return mapReduce.runCommand(collection, rchildren.filters(), rowJob) ;
	}
}
