package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Map;

import net.ion.framework.util.ChainMap;

public interface InListNode extends IPropertyFamily, Serializable{
	InListQuery createQuery();

	InListNode push(ChainMap values);
	InListNode push(Map<String, ? extends Object> values);
	
	InListNode pull(ChainMap values);
	InListNode pull(Map<String, ? extends Object> map);

	public Object get(int index) ;
	public int size() ;

}
