package net.ion.radon.repository.innode;

import net.ion.radon.repository.NodeObject;

public class OrFilter implements InNodeFilter{

	private InNodeFilter[] qs ;
	private OrFilter(InNodeFilter[] qs) {
		this.qs = qs ;
	}

	public static InNodeFilter create(InNodeFilter[] qs) {
		return new OrFilter(qs);
	}

	public boolean isTrue(NodeObject node) {
		for(InNodeFilter query : qs){
			if (query.isTrue(node)) return true ;
		}
		return false;
	}

}
