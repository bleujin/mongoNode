package net.ion.radon.repository.collection;

import java.util.Map.Entry;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoMapEntryDBOSerializer<K, V> implements DBOSerializer<Entry<K, V>> {

	private DBCollection collection;
	private DBOSerializer<K> keySerializer;
	private DBOSerializer<V> valueSerializer;

	public MongoMapEntryDBOSerializer(final DBCollection collection, final DBOSerializer<K> keySerializer, final DBOSerializer<V> valueSerializer) {
		this.collection = collection;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
	}

	public DBObject toDBObject(final Entry<K, V> element, final boolean equalFunctions, final boolean negate) {
		DBObject obj = keySerializer.toDBObject(element.getKey(), equalFunctions, negate);
		obj.putAll(valueSerializer.toDBObject(element.getValue(), equalFunctions, negate));

		return obj;
	}

	public MongoMapEntry<K, V> toElement(final DBObject dbObject) {
		return new MongoMapEntry<K, V>(keySerializer.toElement(dbObject), collection, dbObject, valueSerializer);
	}

}
