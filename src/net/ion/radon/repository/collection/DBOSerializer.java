package net.ion.radon.repository.collection;

import com.mongodb.DBObject;

public interface DBOSerializer<E> {

	DBObject toDBObject(E element, boolean equalFunctions, boolean negate);

	E toElement(DBObject dbObject);

}
