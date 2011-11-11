package net.ion.radon.repository;

import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class PropertyFamily implements IPropertyFamily{
	
	private static final long serialVersionUID = 5606467563983898492L;
	private NodeObject nobject ;
	public final static PropertyFamily BLANK = new PropertyFamily() ;
	private PropertyFamily(){
		nobject = NodeObject.create() ;
	}

	static PropertyFamily create(DBObject dbo) {
		PropertyFamily newThis = new PropertyFamily();
		newThis.nobject = NodeObject.load(dbo) ;
		return newThis;
	}
	
	public static PropertyFamily create() {
		return new PropertyFamily() ;
	}
	
	public static PropertyFamily create(String key, Object value) {
		PropertyFamily newThis = new PropertyFamily();
		newThis.put(key, value) ;
		return newThis ;
	}

	public static PropertyFamily create(Map<String, String> map) {
		PropertyFamily newThis = new PropertyFamily() ;
		for (Entry<String, String> entry : map.entrySet()) {
			newThis.put(entry.getKey(), entry.getValue()) ;
		}
		return create(new BasicDBObject(map));
	}

	public static PropertyFamily createByMap(Map<String, Object> map) {
		
		PropertyFamily newThis = new PropertyFamily() ;
		for (Entry<String, Object> entry : map.entrySet()) {
			// if (entry.getKey().startsWith("aradon.")) continue ;
			Object value = entry.getValue();
			if (value instanceof String && NumberUtil.isNumber(StringUtil.toString(value))) {
				value = Long.parseLong(StringUtil.toString(value)) ;
			}
			newThis.put(entry.getKey(), value) ;
		}
		
		return newThis;
	}

	public PropertyFamily put(String key, Object val){
		nobject.put(key, val) ;
		return this;
	}

	public DBObject getDBObject() {
		return nobject.getDBObject();
	}

	public Map<String, ? extends Object> toMap() {
		return nobject.toMap();
	}
	
	public String toJSONString(){
		return nobject.getDBObject().toString() ;
	}


}
