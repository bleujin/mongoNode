package net.ion.radon.repository.collection;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoConcurrentMap<K, V> implements ConcurrentMap<K, V> {

	private static final BasicDBObject EMPTY_BASICDBOBJECT = new BasicDBObject();
	private DBCollection collection;
	private DBOSerializer<K> keySerializer;
	private DBOSerializer<V> valueSerializer;

	MongoConcurrentMap(final DBCollection collection, final DBOSerializer<K> keySerializer, final DBOSerializer<V> valueSerializer) {
		this.collection = collection;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
	}

	public int size() {
		return (int) collection.count(keySerializer.groupQuery());
	}

	public boolean isEmpty() {
		return size() <= 0;
	}

	@SuppressWarnings("unchecked")
	public boolean containsKey(final Object key) {
		return collection.count(keySerializer.toDBObject((K) key)) > 0;
	}

	@SuppressWarnings("unchecked")
	public boolean containsValue(final Object value) {
		return  collection.count(valueSerializer.groupQuery()) > 0 && collection.count(valueSerializer.toDBObject((V) value)) > 0;
	}

	public void clear() {
		collection.remove(keySerializer.groupQuery());
	}

	public KeyMongoCollection<K> keySet() {
		return new KeyMongoCollection<K>(collection, keySerializer);
	}

	public MongoCollection<V> values() {
		return new EntryMongoCollection<V>(collection, valueSerializer);
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return new EntryMongoCollection<Entry<K, V>>(collection, new MongoMapEntryDBOSerializer<K, V>(collection, keySerializer, valueSerializer));
	}

	@SuppressWarnings("unchecked")
	public V get(final Object key) {
		DBObject result = collection.findOne(keySerializer.toDBObject((K) key));
		return result != null ? valueSerializer.toElement(result) : null;
	}

	public V put(final K key, final V value) {
		return put(key, value, true, false);
	}

	@SuppressWarnings("unchecked")
	public void putAll(final Map<? extends K, ? extends V> map) {
		for (Map.Entry<K, V> entry : ((Map<K, V>) map).entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public V putIfAbsent(final K key, final V value) {
		if (!containsKey(key)) {
			return put(key, value, true, true);
		}
		return get(key);
	}

	public V put(final K key, final V value, boolean insertIfAbsent, boolean returnNew) {

		DBObject queryObject = keySerializer.toDBObject(key);
		
		DBObject dbObject = keySerializer.toDBObject(key);
		dbObject.putAll(valueSerializer.toDBObject(value));

		
		dbObject = collection.findAndModify(queryObject, null, null, false, dbObject, returnNew, insertIfAbsent);
		V old = null;
		if (dbObject != null) {
			old = valueSerializer.toElement(dbObject);
		}

		return old;
	}

	@SuppressWarnings("unchecked")
	public V remove(final Object key) {
		DBObject queryObject = keySerializer.toDBObject((K) key);
		DBObject result;
		V old = null;

		result = collection.findAndRemove(queryObject);
		if (result != null) {
			old = valueSerializer.toElement(result);
		}

		return old;
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object key, final Object value) {
		DBObject queryObject = keySerializer.toDBObject((K) key);
		queryObject.putAll(valueSerializer.toDBObject((V) value));

		return collection.remove(queryObject).getN() > 0;
	}

	public boolean replace(final K key, final V oldValue, final V newValue) {
		DBObject queryObject = keySerializer.toDBObject(key);
		DBObject dbObject = keySerializer.toDBObject(key);

		queryObject.putAll(valueSerializer.toDBObject(oldValue));
		dbObject.putAll(valueSerializer.toDBObject(newValue));

		return collection.update(queryObject, dbObject).getN() > 0;
	}

	public V replace(final K key, final V value) {
		return put(key, value, false, false);
	}

}


class EntryMongoCollection<T> extends MongoCollection<T> implements Set<T> {

	EntryMongoCollection(DBCollection collection, DBOSerializer<T> serializer) {
		super(collection, serializer);
	}

	@Override
	public boolean add(T e) {
		return super.add(e) ;
	}
}

class KeyMongoCollection<T> extends MongoCollection<T> implements Set<T> {
	

	public KeyMongoCollection(DBCollection collection, DBOSerializer<T> keyserializer) {
		super(collection, keyserializer);
	}

	@Override
	public boolean add(T e) {
		if (!contains(e)) {
			return super.add(e);
		}
		return false;
	}

}
