package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

import org.apache.commons.lang.ArrayUtils;

public class InFilter implements InNodeFilter{

	private String path ;
	private Object[] values ;

	private InFilter(String path, Object[] values) {
		this.path = path ;
		this.values = values ;
	}

	public static InFilter create(String path, Object[] values) {
		return new InFilter(path, values);
	}

	public boolean isTrue(NodeObject node) {
		return ArrayUtils.contains(values, node.get(path)) ;
	}

}
