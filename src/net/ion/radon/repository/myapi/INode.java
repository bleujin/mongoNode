package net.ion.radon.repository.myapi;

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeResult;

public interface INode {

	public String getIdentifier() ;
	public NodeResult save() ;
	public String getName() ;
	public Object getId() ;
	public INode setProperty(String pkey, IValue value) ;
	public INode setProperty(String pkey, IValue[] value) ;
	public IProperty getProperty(String pkey, IValue[] value) ;
	
	// child
	public INode createChild(String name);
	public List<INode> getChild(PageBean page);
	public List<INode> getChild(String name, PageBean page);
	public List<INode> removeChild(String nameOrId);
	public INode remove() ;
}
