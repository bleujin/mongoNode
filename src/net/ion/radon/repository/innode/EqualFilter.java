package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;


public class EqualFilter implements InNodeFilter{

	private String path ;
	private Object value ;
	private EqualFilter(String path, Object value) {
		this.path = path ;
		this.value = value ;
	}

	public final static EqualFilter create(String path, Object value){
		return new EqualFilter(path, value) ;
	}
	
	public boolean isTrue(NodeObject no) {
		return value != null && value.equals(no.get(path));
	}

}
