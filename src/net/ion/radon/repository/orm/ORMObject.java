package net.ion.radon.repository.orm;

import java.io.Serializable;

public interface ORMObject extends Serializable{

	public void put(String key, Object value) ;
}



