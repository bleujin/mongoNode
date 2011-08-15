package net.ion.radon.repository.orm;

import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.NodeObject;

public interface ORMObject {

	public void put(String key, Object value) ;
	public NodeObject getNodeObject() ;
}



