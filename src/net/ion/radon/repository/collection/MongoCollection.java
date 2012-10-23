package net.ion.radon.repository.collection;

import java.util.Collection;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectId;
import net.ion.radon.repository.AradonId;
import net.ion.radon.repository.NodeConstants;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class MongoCollection<T> implements Collection<T> {

	private DBCollection collection;
	private DBOSerializer<T> serializer;

	protected MongoCollection(final DBCollection collection, final DBOSerializer<T> serializer) {
		this.collection = collection;
		this.serializer = serializer;
	}

	protected DBCollection getCollection() {
		return collection;
	}
	
	protected DBOSerializer<T> getSerializer(){
		return serializer ;
	}

	public int size() {
		return (int) collection.count(serializer.groupQuery());
	}

	public void clear() {
		collection.remove(serializer.groupQuery());
	}

	public boolean isEmpty() {
		return size() <= 0;
	}

	@SuppressWarnings("unchecked")
	public boolean contains(final Object o) {
		return collection.count(serializer.toDBObject((T) o)) > 0;
	}

	public CloseableIterator<T> iterator() {
		return new CloseableIterator<T>() {

			DBCursor cursor = collection.find(serializer.groupQuery());

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
		return toArray(new Object[0]) ;
	}

	public <V> V[] toArray(final V[] a) {
		CloseableIterator<T> iter = iterator();
		List<V> list = ListUtil.newList() ;
		while(iter.hasNext()){
			list.add((V)iter.next()) ;
		}

		return list.toArray(a) ;
	}

	public boolean add(final T e) {
		final DBObject dbObject = serializer.toDBObject(e);
		dbObject.put(NodeConstants.ARADON, AradonId.create(serializer.groupId(), new ObjectId().toString()).getDBObject()) ;
		final WriteResult wr = collection.insert(dbObject);
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object o) {
		return collection.remove(serializer.toDBObject((T) o)).getN() > 0;
	}

	public boolean addAll(final Collection<? extends T> cols) {
		boolean changed = false;

		for (T e : cols) {
			if (add(e)) {
				changed = true;
			}
		}

		return changed;
	}

	public boolean containsAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

}

