package net.ion.radon.repository.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class CachingConcurrentMap<K, V> extends CachingMap<K, V> implements ConcurrentMap<K, V> {

	private Object monitor = new Object();

	public CachingConcurrentMap(final ConcurrentMap<K, V> cache, final MongoConcurrentMap<K, V> backstore) {
		super(cache, backstore);
	}

	@Override
	public void clear() {
		synchronized (monitor) {
			super.clear();
		}
	}

	@Override
	public boolean containsKey(final Object key) {
		synchronized (monitor) {
			return super.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(final Object value) {
		synchronized (monitor) {
			return super.containsValue(value);
		}
	}

	@Override
	public V get(final Object key) {
		synchronized (monitor) {
			return super.get(key);
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (monitor) {
			return super.isEmpty();
		}
	}

	@Override
	public V put(final K key, final V value) {
		synchronized (monitor) {
			return super.put(key, value);
		}
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> map) {
		synchronized (monitor) {
			super.putAll(map);
		}
	}

	@Override
	public V remove(final Object key) {
		synchronized (monitor) {
			return super.remove(key);
		}
	}

	public V putIfAbsent(final K key, final V value) {
		synchronized (monitor) {
			if (!super.containsKey(key)) {
				return super.put(key, value);
			}
			return super.get(key);
		}
	}

	public boolean remove(final Object key, final Object value) {
		synchronized (monitor) {
			if (super.containsKey(key) && super.get(key).equals(value)) {
				super.remove(key);
				return true;
			}
			return false;
		}
	}

	public V replace(final K key, final V value) {
		synchronized (monitor) {
			if (super.containsKey(key)) {
				return super.put(key, value);
			}
			return null;
		}
	}

	public boolean replace(final K key, final V oldValue, final V newValue) {
		synchronized (monitor) {
			if (super.containsKey(key) && super.get(key).equals(oldValue)) {
				super.put(key, newValue);
				return true;
			}
			return false;
		}
	}

}
