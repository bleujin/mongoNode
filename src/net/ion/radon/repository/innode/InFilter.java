package net.ion.radon.repository.innode;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import net.ion.radon.repository.InNode;
import net.ion.radon.repository.NodeObject;

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
