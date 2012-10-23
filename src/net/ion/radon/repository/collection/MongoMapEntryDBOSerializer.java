package net.ion.radon.repository.collection;

import java.util.Map.Entry;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoMapEntryDBOSerializer<K, V> implements DBOSerializer<Entry<K, V>> {

	private DBCollection collection;
	private DBOSerializer<K> keySerializer;
	private DBOSerializer<V> valueSerializer;

	MongoMapEntryDBOSerializer(final DBCollection collection, final DBOSerializer<K> keySerializer, final DBOSerializer<V> valueSerializer) {
		this.collection = collection;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
	}

	public DBObject toDBObject(final Entry<K, V> element) {
		DBObject obj = keySerializer.toDBObject(element.getKey());
		obj.putAll(valueSerializer.toDBObject(element.getValue()));

		return obj;
	}

	public MongoMapEntry<K, V> toElement(final DBObject dbObject) {
		return new MongoMapEntry<K, V>(keySerializer.toElement(dbObject), collection, dbObject, valueSerializer);
	}

	public DBObject groupQuery(){
		return valueSerializer.groupQuery() ;
	}
	public String groupId(){
		return valueSerializer.groupId() ;
	}

}
