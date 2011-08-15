package net.ion.radon.repository.myapi;

import java.util.List;

public interface IProperty {

	public void setValue(IValue value) ;
	public INode getNode() ;
	public String getString() ;
	public IValue getValue() ;
	public List<IValue> getValues() ;
}
