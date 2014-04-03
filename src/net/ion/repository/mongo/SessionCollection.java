package net.ion.repository.mongo;

import org.apache.ecs.xhtml.s;

import net.ion.framework.util.Debug;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

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
}
