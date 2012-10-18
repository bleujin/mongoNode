package net.ion.radon.repository.collection;

import java.util.Iterator;

public interface CloseableIterator<T> extends Iterator<T> {

	void close();

}
