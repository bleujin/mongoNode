package net.ion.radon.repository.myapi;

import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyFamily;


public interface INodeCursor extends ICursor{
	public NodeCursor sort(PropertyFamily family) ;
	public NodeCursor ascending(String... propIds);
	public NodeCursor descending(String... propIds);
}
