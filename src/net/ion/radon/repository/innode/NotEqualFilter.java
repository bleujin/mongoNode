package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class NotEqualFilter implements InNodeFilter{

	private String path ;
	private Object value ;
	private NotEqualFilter(String path, Object value) {
		this.path = path ;
		this.value = value ;
	}

	public final static NotEqualFilter create(String path, Object value){
		return new NotEqualFilter(path, value) ;
	}
	
	public boolean isTrue(NodeObject no) {
		return value != null && ! value.equals(no.get(path));
	}

}