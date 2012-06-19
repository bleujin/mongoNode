package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class AndFilter implements InNodeFilter{

	private InNodeFilter[] qs ;
	private AndFilter(InNodeFilter[] qs) {
		this.qs = qs ;
	}

	public static InNodeFilter create(InNodeFilter[] qs) {
		return new AndFilter(qs);
	}

	public boolean isTrue(NodeObject node) {
		for (InNodeFilter query : qs) {
			if (! query.isTrue(node)) return false ;
		}
		return true;
	}

}
