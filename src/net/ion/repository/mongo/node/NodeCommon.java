package net.ion.repository.mongo.node;

import java.util.Map;
import java.util.Set;

import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.ISession;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;

import com.google.common.base.Function;

public interface NodeCommon<T extends NodeCommon<T>>  {

	public ISession<T> session() ;

	public Fqn fqn();

	public int dataSize();

	public T parent();

	public boolean hasChild(String fqn);
	
	public boolean hasProperty(PropertyId pid) ;

	PropertyValue extendProperty(String propPath);

	public T root() ;

	public T child(String fqn);

	public Set<String> childrenNames();

	public Set<PropertyId> keys();
	
	public Set<PropertyId> normalKeys();

	public PropertyValue property(String key);
	
	public PropertyValue propertyId(PropertyId pid) ;
	
	public Object id() ;

	Map<PropertyId, PropertyValue> toMap();

	public T ref(String refName) ;
	
	boolean hasRef(String refName);
	
	boolean hasRef(String refName, Fqn fqn);

	public IteratorList<T> refs(String refName) ;

	<R> R transformer(Function<T, R> transformer) ;
}
