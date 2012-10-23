package net.ion.radon.repository.collection;

import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.DBCollection;

public class CollectionFactory {

	private DBCollection collection ;
	private String groupId ;
	public CollectionFactory(DBCollection collection, String groupId) {
		this.collection = collection ;
		this.groupId = groupId ;
	}

	public <K, V> MongoConcurrentMap<K, V> newConcurrentMap(Class<K> key, Class<V> value) {
		return new MongoConcurrentMap<K, V>(collection, createKeyDefaultSerializer(groupId, key), createDefaultSerializer("value", value)) ;
	}

	public <K, V> CachingConcurrentMap<K, V> newCacheConcurrentMap(MongoConcurrentMap<K, V> backer) {
		return new CachingConcurrentMap<K, V>(new ConcurrentHashMap<K, V>(), backer) ;
	}

	public <E> MongoQueue<E> newQueue(Class<E> value) {
		return new MongoQueue<E>(collection, createDefaultSerializer("value", value), true) ;
	}

	public <E> MongoQueue<E> newStack(Class<E> value) {
		return new MongoQueue<E>(collection, createDefaultSerializer("value", value), false) ;
	}


	
	
	private <V> KeyDBOSerializer<V> createKeyDefaultSerializer(String groupId, Class<V> value) {
		return new KeyDBOSerializer<V>(groupId, value);
	}

	private <V> DefaultDBOSerializer<V> createDefaultSerializer(String fieldName, Class<V> value) {
		return new DefaultDBOSerializer<V>(groupId, fieldName, value);
	}


}
