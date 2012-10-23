package net.ion.radon.repository.collection;

import java.util.Map;
import java.util.Set;

public class CachingMap<K, V> implements Map<K, V> {

	private Map<K, V> cache;
	private MongoConcurrentMap<K, V> backstore;

	public CachingMap(final Map<K, V> cache, final MongoConcurrentMap<K, V> backstore) {
		this.cache = cache;
		this.backstore = backstore;
	}

	public void clear() {
		cache.clear();
		backstore.clear();
	}

	public boolean containsKey(final Object key) {
		if (!cache.containsKey(key)) {
			return backstore.containsKey(key);
		}
		return true;
	}

	public boolean containsValue(final Object value) {
		if (!cache.containsValue(value)) {
			return backstore.containsValue(value);
		}
		return true;
	}

	public Set<Entry<K, V>> entrySet() {
		return backstore.entrySet();
	}

	public V get(final Object key) {
		if (!cache.containsKey(key)) {
			if (backstore.containsKey(key)) {
				return backstore.get(key);
			}
			return null;
		}
		return cache.get(key);
	}

	public boolean isEmpty() {
		return cache.isEmpty() ? backstore.isEmpty() : false;
	}

	public Set<K> keySet() {
		return backstore.keySet();
	}

	public V put(final K key, final V value) {
		cache.put(key, value);
		return backstore.put(key, value);
	}

	public void putAll(final Map<? extends K, ? extends V> map) {
		cache.putAll(map);
		backstore.putAll(map);
	}

	public V remove(final Object key) {
		cache.remove(key);
		return backstore.remove(key);
	}

	public int size() {
		return backstore.size();
	}

	public MongoCollection<V> values() {
		return backstore.values();
	}

}
