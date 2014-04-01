package net.ion.repository.mongo.node;


public interface ReadChildrenEach<T> {
	public T handle(ReadChildrenIterator citer) ;
}
