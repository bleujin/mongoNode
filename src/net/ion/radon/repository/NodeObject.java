package net.ion.radon.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.util.JSONUtil;
import net.sf.json.JSONObject;

import org.apache.commons.lang.CharUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class NodeObject implements Serializable, IPropertyFamily {
	private static final long serialVersionUID = -321332758287996204L;
	private DBObject inner ;

	final static NodeObject BLANK_INNODE = NodeObject.load(new BasicDBList()) ; 
	
	private NodeObject(){
		inner = new BasicDBObject() ;
	}
	public static NodeObject load(DBObject dbo) {
		NodeObject newObject = new NodeObject();
		newObject.inner = dbo ;
		return newObject;
	}
	
	public final static NodeObject create(){
		return new NodeObject() ;
	}

	public static NodeObject create(String key, Object value) {
		NodeObject newObject = create() ;
		newObject.put(key, value) ;
		
		return newObject;
	}
	
	@Override
	public String toString() {
		return inner.toString();
	}
	
	public void put(String key, Object value){
		if (value instanceof IPropertyFamily){
			inner.put(key.toLowerCase(), ((IPropertyFamily)value).getDBObject()) ;
		} else if (value instanceof JSONObject){
			inner.put(key.toLowerCase(), JSONUtil.toDBObject((JSONObject)value)) ;
		} else {
			inner.put(key.toLowerCase(), value) ;
		}
	}
	
	public void put(String key, String[] values){
		BasicDBList list = new BasicDBList() ;
		for(int i=0; i < values.length; i++){
			list.add(values[i]) ;
		}
		put(key, list) ;
	}

	public void putProperty(PropertyId pid, Object value){
		put(pid.getKeyString(), value) ;
	}

	public Object get(String key){
		String[] propIds = StringUtil.split(key, ".") ;
		
		int i = 0 ;
		
		Object result = null ;
		DBObject about = inner ;
		while(i < propIds.length){
			result = about.get(propIds[i].toLowerCase());
			if (result != null && result instanceof DBObject) {
				about = (DBObject) result ;
				i++ ;
				continue ;
			}
			else break ;
		}
		
		return result ;
	}

	public String getString(String key) {
		return StringUtil.toString(get(key));
	}

	public synchronized void appendProperty(PropertyId pid, Object val) {
		if (containsField(pid.getKeyString())) {
			Serializable beforeVal = (Serializable)get(pid.getKeyString());
			if (beforeVal instanceof List) {
				((List) beforeVal).add(val);
			} else {
				List vals = ListUtil.newList();
				vals.add(beforeVal);
				vals.add(val);
				putProperty(pid, vals);
			}
		} else {
			putProperty(pid, val);
		}
		
	}
	
	public boolean containsField(String key) {
		return inner.containsField(key.toLowerCase());
	}

	public DBObject getDBObject() {
		return inner;
	}

	public int size() {
		return toMap().size();
	}

	public Map toMap() {
		return inner.toMap();
	}


	
}


class PropertyId {

	private String innerKey ;
	private PropertyId(String key){
		this.innerKey = key.toLowerCase() ;
	}
	
	public static PropertyId create(String key) {
		checkAllowedPropertyName(key) ;
		return new PropertyId(key);
	}
	
	private static void checkAllowedPropertyName(String pkey) {
		if (StringUtil.isBlank(pkey) || NodeUtil.isReservedProperty(pkey)) {
		// if (StringUtil.isBlank(pkey)) {
			throw new IllegalArgumentException("illegal property id :" + pkey);
		}
		if (CharUtils.isAsciiNumeric(pkey.charAt(0))) 
			throw new IllegalArgumentException("illegal property id :" + pkey);
	}
	
	public String getKeyString(){
		return innerKey ;
	}

	public static PropertyId reserved(String key) {
		if (! key.startsWith("_"))
			throw new IllegalArgumentException("illegal reserved property id :" + key);

		return new PropertyId(key);
	}
}
