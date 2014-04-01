package net.ion.repository.mongo.node;

import java.util.Iterator;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.WriteSession;

public class ReadChildrenIterator implements Iterable<ReadNode>, Iterator<ReadNode>{

	private ReadSession rsession;
	private DBCursor cursor;

	ReadChildrenIterator(ReadSession rsession, DBCursor cursor) {
		this.rsession = rsession ;
		this.cursor = cursor ;
	}

	final static ReadChildrenIterator create(ReadSession rsession, DBCursor cursor){
		return new ReadChildrenIterator(rsession, cursor) ;
	}
	
	@Override
	public Iterator<ReadNode> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return cursor.hasNext();
	}

	@Override
	public ReadNode next() {
		DBObject dbo = cursor.next() ;
		return ReadNode.create(rsession, Fqn.fromDBObject(dbo), dbo);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("readonly") ;
	}


}
