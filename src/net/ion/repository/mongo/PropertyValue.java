package net.ion.repository.mongo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

public class PropertyValue {

	private Object val;
	public final static PropertyValue NotFound = new PropertyValue("");
	
	private PropertyValue(Object val) {
		this.val = val ;
	}

	public static PropertyValue create(Object val) {
		if (val == null) return PropertyValue.NotFound ;
		return new PropertyValue(val);
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
	
	public Object value(){
		return asObject() ;
	}
	

	public int compareTo(PropertyValue that) {
		if (this.value() instanceof Comparable && that.asObject() instanceof Comparable) {
			return ((Comparable) this.asObject()).compareTo(that.asObject());
		}
		return 0;
	}

	public Set asSet() {
		HashSet result = new HashSet();
		result.add(val);
		return result ;
	}

	public int size() {
		return 1;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this) ;
	}


}
