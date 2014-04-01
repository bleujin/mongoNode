package net.ion.repository.mongo.node;

public interface WriteChildrenEach<T> {
	public T handle(WriteChildrenIterator citer) ;
}
