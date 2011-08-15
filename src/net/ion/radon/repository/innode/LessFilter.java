package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class LessFilter implements InNodeFilter {

	private String path;
	private Object value;

	private LessFilter(String path, Object value) {
		this.path = path;
		this.value = value;
	}

	public final static LessFilter create(String path, Object value) {
		return new LessFilter(path, value);
	}

	public boolean isTrue(NodeObject no) {
		return value != null && ((Comparable) value).compareTo(no.get(path)) > 0;
	}

}