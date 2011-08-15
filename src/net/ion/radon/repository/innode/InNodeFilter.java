package net.ion.radon.repository.innode;

import net.ion.radon.repository.InNode;
import net.ion.radon.repository.NodeObject;

public interface InNodeFilter {
	public boolean isTrue(NodeObject node) ;
}
