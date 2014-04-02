package net.ion.repository.mongo.node;

import java.util.Set;

import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.ExtendPropertyId;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyId.PType;
import net.ion.repository.mongo.PropertyValue;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class AbstractNode<T extends NodeCommon<T>> implements NodeCommon<T>{
	
	private DBObject found ;
	protected AbstractNode(DBObject found) {
		this.found = found ;
	}
	
	public Set<PropertyId> normalKeys() {
		Set<PropertyId> result = SetUtil.newSet() ;
		for (String key : found.keySet()){
			if (key.startsWith("_") || key.startsWith("@")) continue ;
			
			PropertyId pid = PropertyId.fromString(key);
			if (pid.type() == PType.NORMAL) result.add(pid) ;
		} 
		return result;
	}
	
	public PropertyValue property(String name) {
		return propertyId(PropertyId.fromString(name));
	}
	
	public PropertyValue propertyId(PropertyId pid) {
		return PropertyValue.create(found().get(pid.fullString()));
	}


	public DBObject found() {
		return found;
	}
	
	
	@Override
	public PropertyValue extendProperty(String propPath) {
		return ExtendPropertyId.create(propPath).propValue(this) ;
	}

	@Override
	public boolean hasProperty(PropertyId pid) {
		return found().containsField(pid.fullString());
	}
	
	@Override
	public Set<PropertyId> keys() {
		Set<PropertyId> result = SetUtil.newSet() ;
		for (String key : found().keySet()){
			result.add(PropertyId.fromString(key)) ;
		} 
		return result;
	}
	

	@Override
	public Object id() {
		return fqn();
	}


	public long getLastModified() {
		return property("_lastmodified").asLong();
	}



	@Override
	public boolean hasRef(String refName) {
		return found().containsField(PropertyId.refer(refName).fullString());
	}

	@Override
	public boolean hasRef(String refName, Fqn fqn) {
		PropertyValue findProp = propertyId(PropertyId.refer(refName)) ;
		Set<String> set = findProp.asSet() ;
		
		return set.contains(fqn.toString());
	}
	
	
	public int propSize(){
		return normalKeys().size() ;
	}

	protected void found(BasicDBObject newOb) {
		this.found = newOb ;
	}
	
}
