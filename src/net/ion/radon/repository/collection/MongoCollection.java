package net.ion.radon.repository.collection;

import java.util.Collection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MongoCollection<T> implements Collection<T> {

	private DBOSerializer<T> serializer;
	private DBCollection collection;

	public MongoCollection(final DBCollection collection, final DBOSerializer<T> serializer) {
		this.collection = collection;
		this.serializer = serializer;
	}

	protected DBOSerializer<T> getSerializer() {
		return serializer;
	}

	protected DBCollection getCollection() {
		return collection;
	}

	public int size() {
		return (int) collection.count();
	}

	public boolean isEmpty() {
		return size() <= 0;
	}

	@SuppressWarnings("unchecked")
	public boolean contains(final Object o) {
		return collection.count(serializer.toDBObject((T) o, true, false)) > 0;
	}

	public CloseableIterator<T> iterator() {
		return new CloseableIterator<T>() {

			DBCursor cursor = collection.find();

			public boolean hasNext() {
				boolean next = cursor.hasNext();
				if (!next) {
					cursor.close();
				}
				return next;
			}

			public T next() {
				return serializer.toElement(cursor.next());
			}

			public void remove() {
				cursor.remove();
			}

			public void close() {
				cursor.close();
			}

		};
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <V> V[] toArray(final V[] a) {
		throw new UnsupportedOperationException();
	}

	public boolean add(final T e) {
		return collection.insert(serializer.toDBObject(e, false, false)).getN() > 0;
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object o) {
		return collection.remove(serializer.toDBObject((T) o, true, false)).getN() > 0;
	}

	public boolean containsAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(final Collection<? extends T> c) {
		boolean changed = false;

		for (T e : c) {
			if (add(e)) {
				changed = true;
			}
		}

		return changed;
	}

	public boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		collection.remove(new BasicDBObject());
	}

}
