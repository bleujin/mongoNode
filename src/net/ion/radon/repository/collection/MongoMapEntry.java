package net.ion.radon.repository.collection;

import java.util.Map.Entry;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoMapEntry<K, V> implements Entry<K, V> {

	DBCollection collection;
	DBObject document;
	K key;
	DBOSerializer<V> serializer;

	public MongoMapEntry(final K key, final DBCollection collection, final DBObject document, final DBOSerializer<V> serializer) {
		this.document = document;
		this.collection = collection;
		this.key = key;
		this.serializer = serializer;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return serializer.toElement(document);
	}

	public V setValue(final V value) {
		V old = getValue();
		document.putAll(serializer.toDBObject(value, false, false));
		collection.save(document);
		return old;
	}

}
