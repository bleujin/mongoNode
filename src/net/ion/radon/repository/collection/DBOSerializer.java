package net.ion.radon.repository.collection;

import com.mongodb.DBObject;

public interface DBOSerializer<E> {

	DBObject toDBObject(E element);

	E toElement(DBObject dbObject);

	public DBObject groupQuery() ;

	String groupId();
}
