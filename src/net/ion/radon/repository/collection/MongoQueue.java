package net.ion.radon.repository.collection;

import java.util.NoSuchElementException;
import java.util.Queue;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoQueue<E> extends MongoCollection<E> implements Queue<E> {

	private static final BasicDBObject ORDER_BY_ASC = new BasicDBObject("_id", 1);
	private static final BasicDBObject ORDER_BY_DESC = new BasicDBObject("_id", -1);

	private boolean asc;

	public MongoQueue(final DBCollection collection, final DBOSerializer<E> serializer) {
		super(collection, serializer);
		asc = true;
	}

	public MongoQueue(final DBCollection collection, final DBOSerializer<E> serializer, final boolean asc) {
		super(collection, serializer);
		this.asc = asc;
	}

	public CloseableIterator<E> iterator() {
		return new CloseableIterator<E>() {

			DBCursor cursor = getCollection().find().sort(asc ? ORDER_BY_ASC : ORDER_BY_DESC);

			public boolean hasNext() {
				boolean next = cursor.hasNext();
				if (!next) {
					cursor.close();
				}
				return next;
			}

			public E next() {
				return getSerializer().toElement(cursor.next());
			}

			public void remove() {
				cursor.remove();
			}

			public void close() {
				cursor.close();
			}

		};
	}

	public E remove() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return poll();
	}

	public boolean offer(final E e) {
		return add(e);
	}

	public E poll() {
		DBObject dbObject;

		if (isEmpty()) {
			return null;
		}

		dbObject = getCollection().findAndModify(null, null, asc ? ORDER_BY_ASC : ORDER_BY_DESC, true, null, false, false);
		return getSerializer().toElement(dbObject);
	}

	public E element() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return peek();
	}

	public E peek() {
		DBCursor cursor;

		if (isEmpty()) {
			return null;
		}

		cursor = getCollection().find().sort(asc ? ORDER_BY_ASC : ORDER_BY_DESC);
		try {
			if (cursor.hasNext()) {
				return getSerializer().toElement(cursor.next());
			}
			return null;
		} finally {
			cursor.close();
		}
	}

}
