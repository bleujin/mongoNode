package net.ion.repository.mongo;

public interface WriteJob<T> {
	public T handle(WriteSession wsession) throws Exception ;
}
