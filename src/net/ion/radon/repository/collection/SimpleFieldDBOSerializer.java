package net.ion.radon.repository.collection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class SimpleFieldDBOSerializer<E> implements DBOSerializer<E> {

	private String field;

	public SimpleFieldDBOSerializer(final String field) {
		this.field = field;
	}

	public DBObject toDBObject(final E element, final boolean equalFunctions, final boolean negate) {
		if (equalFunctions && negate) {
			return new BasicDBObject(field, new BasicDBObject("$ne", element));
		}
		return new BasicDBObject(field, element);
	}

	@SuppressWarnings("unchecked")
	public E toElement(final DBObject dbObject) {
		return (E) dbObject.get(field);
	}

}
