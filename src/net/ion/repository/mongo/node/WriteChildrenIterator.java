package net.ion.repository.mongo.node;

import java.util.Iterator;

import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.WriteSession;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class WriteChildrenIterator implements Iterable<WriteNode>, Iterator<WriteNode>{

	private WriteSession wsession;
	private DBCursor cursor;

	WriteChildrenIterator(WriteSession rsession, DBCursor cursor) {
		this.wsession = rsession ;
		this.cursor = cursor ;
	}

	final static WriteChildrenIterator create(WriteSession wsession, DBCursor cursor){
		return new WriteChildrenIterator(wsession, cursor) ;
	}
	
	@Override
	public Iterator<WriteNode> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return cursor.hasNext();
	}

	@Override
	public WriteNode next() {
		DBObject dbo = cursor.next() ;
		return WriteNode.create(wsession, Fqn.fromDBObject(dbo), dbo);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("readonly") ;
	}
	
	public Explain explain(){
		return wsession.attribute(Explain.class.getCanonicalName(), Explain.class) ;
	}

}
