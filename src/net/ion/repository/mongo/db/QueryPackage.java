package net.ion.repository.mongo.db;

import net.ion.repository.mongo.ReadSession;

public abstract class QueryPackage {

	private ReadSession session ;
	
	protected ReadSession session() {
		if (session == null) throw new UnsupportedOperationException();
		return session ;
	}
	
}
