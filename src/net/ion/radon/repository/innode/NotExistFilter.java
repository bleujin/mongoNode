package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class NotExistFilter implements InNodeFilter{

	private String path ;
	private NotExistFilter(String path) {
		this.path = path ;
	}

	public static InNodeFilter create(String path) {
		return new NotExistFilter(path);
	}

	public boolean isTrue(NodeObject node) {
		return node.get(path) == null;
	}

}
