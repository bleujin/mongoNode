package net.ion.repository.mongo;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

import com.mongodb.BasicDBList;

public class PropertyValue {

	private Object val;
	private BasicDBList list ;
	public final static PropertyValue NotFound = new PropertyValue(new BasicDBList());
	
	private PropertyValue(Object val) {
		this.val = val ;
		this.list = new BasicDBList() ;
		list.add(val) ;
	}

	private PropertyValue(BasicDBList vals){
		this.val = vals.size() > 0 ? vals.get(0) : "" ;
		this.list = vals ;
	}
	
	public static PropertyValue create(Object val) {
		if (val == null) return PropertyValue.NotFound ;
		if (val instanceof BasicDBList) return new PropertyValue((BasicDBList)val) ;
		return new PropertyValue(val);
	}

	
	
	public boolean isNotFound(){
		return this == NotFound ;
	}
	
	public String asString(){
		return StringUtil.toString(val) ;
	}

	public long asLong() {
		return NumberUtil.toLong(asString(), 0L);
	}

	public int asInt() {
		return NumberUtil.toInt(asString(), 0);
	}

	public boolean asBoolean() {
		return Boolean.valueOf(asString());
	}

	public Date asDate(){
		return new Date(asLong()) ; 
	}
	
	public Object asObject() {
		return val;
	}


	public int compareTo(PropertyValue that) {
		if (this.asObject() instanceof Comparable && that.asObject() instanceof Comparable) {
			return ((Comparable) this.asObject()).compareTo(that.asObject());
		}
		return 0;
	}

	public Set asSet() {
		HashSet result = new HashSet();
		for (Object object : list.toArray()) {
			result.add(object) ;
		}
		return result ;
	}

	public List asList() {
		return list ;
	}

	
	public int size() {
		return list.size();
	}
	
	public String toString(){
		return getClass().getCanonicalName() + (size() == 1 ? ("{" + val + "}")  : list.toString()) ;
	}


}
