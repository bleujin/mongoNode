package net.ion.radon.repository.collection;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.DBCollection;

public class CollectionFactory {

	private DBCollection collection ;
	public CollectionFactory(DBCollection collection) {
		this.collection = collection ;
	}

	public <K, V> MongoConcurrentMap<K, V> newConcurrentMap(Class<K> key, Class<V> value) {
		return new MongoConcurrentMap<K, V>(collection, createDefaultSerializer("key", key), createDefaultSerializer("value", value)) ;
	}
	
	public <E> MongoSet<E> newSet(Class<E> value){
		return new MongoSet<E>(collection, createDefaultSerializer("value", value)) ;
	}

	private <V> DefaultDBOSerializer<V> createDefaultSerializer(String fieldName, Class<V> value) {
		return new DefaultDBOSerializer<V>(fieldName, value);
	}

	public <E> MongoQueue<E> newQueue(Class<E> value) {
		return new MongoQueue<E>(collection, createDefaultSerializer("value", value), true) ;
	}

	public <E> MongoQueue<E> newDescQueue(Class<E> value) {
		return new MongoQueue<E>(collection, createDefaultSerializer("value", value), false) ;
	}

	public <K, V> CachingConcurrentMap<K, V> newCacheConcurrentMap(MongoConcurrentMap<K, V> backer) {
		return new CachingConcurrentMap<K, V>(new ConcurrentHashMap<K, V>(), backer) ;
	}

	// test only
	DBCollection getCollection(){
		return collection ;
	}
}
