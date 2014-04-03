package net.ion.repository.mongo.mr;

import com.mongodb.MapReduceOutput;

public interface RowJob<T> {
	public T handle(MapReduceOutput output) ; 
}
