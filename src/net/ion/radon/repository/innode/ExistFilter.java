package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class ExistFilter implements InNodeFilter{

	private String path ;
	private ExistFilter(String path) {
		this.path = path ;
	}

	public static InNodeFilter create(String path) {
		return new ExistFilter(path);
	}

	public boolean isTrue(NodeObject node) {
		return node.get(path) != null;
	}

}
