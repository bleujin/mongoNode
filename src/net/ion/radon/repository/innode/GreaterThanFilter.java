package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class GreaterThanFilter implements InNodeFilter{

	private String path ;
	private Object value ;
	private GreaterThanFilter(String path, Object value) {
		this.path = path ;
		this.value = value ;
	}

	public final static GreaterThanFilter create(String path, Object value){
		return new GreaterThanFilter(path, value) ;
	}
	
	public boolean isTrue(NodeObject no) {
		return value != null && ((Comparable)value).compareTo(no.get(path)) <= 0;
	}

}