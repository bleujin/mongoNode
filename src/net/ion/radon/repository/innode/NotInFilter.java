package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

import org.apache.commons.lang.ArrayUtils;

public class NotInFilter implements InNodeFilter{


	private String path ;
	private Object[] values ;
	public NotInFilter(String path, Object[] values) {
		this.path = path ;
		this.values = values ;
	}

	public static InNodeFilter create(String path, Object[] values) {
		return new NotInFilter(path, values);
	}

	public boolean isTrue(NodeObject node) {
		return ! ArrayUtils.contains(values, node.get(path)) ;
	}

}
