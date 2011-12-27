package net.ion.radon.repository.orm;

import java.io.Serializable;

import net.ion.radon.repository.NodeObject;

public interface ORMObject extends Serializable{

	public void put(String key, Object value) ;
}



