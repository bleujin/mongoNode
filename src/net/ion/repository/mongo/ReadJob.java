package net.ion.repository.mongo;

import net.ion.repository.mongo.node.ChildrenIterator;

public interface ReadJob<T> {
	public T handle(ChildrenIterator citer) ;
}
