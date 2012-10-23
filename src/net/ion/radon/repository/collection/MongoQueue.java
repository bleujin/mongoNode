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

	private BasicDBObject order;

	public MongoQueue(final DBCollection collection, final DBOSerializer<E> serializer) {
		super(collection, serializer);
		order = ORDER_BY_ASC;
	}

	public MongoQueue(final DBCollection collection, final DBOSerializer<E> serializer, final boolean asc) {
		super(collection, serializer);
		this.order = asc ? ORDER_BY_ASC : ORDER_BY_DESC;
	}

	public CloseableIterator<E> iterator() {
		return new CloseableIterator<E>() {

			DBCursor cursor = getCollection().find(getSerializer().groupQuery()).sort(order);

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
		if (isEmpty()) {
			return null;
		}

		DBObject dbObject = getCollection().findAndModify(getSerializer().groupQuery(), null, order, true, null, false, false);
		return getSerializer().toElement(dbObject);
	}

	public E element() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return peek();
	}

	public E peek() {
		if (isEmpty()) {
			return null;
		}

		DBCursor cursor = getCollection().find(getSerializer().groupQuery()).sort(order);
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
