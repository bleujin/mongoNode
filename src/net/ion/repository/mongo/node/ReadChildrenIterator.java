package net.ion.repository.mongo.node;

import java.util.Iterator;

import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.ReadSession;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
	
	public int count(){
		return cursor.count() ;
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

	
	public Explain explain(){
		return rsession.attribute(Explain.class.getCanonicalName(), Explain.class) ;
	}

}
