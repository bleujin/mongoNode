package net.ion.repository.mongo;

import com.mongodb.DBObject;

public interface Filter {
	public void handle(DBObject filter) ;
}
