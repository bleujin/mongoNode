package net.ion.repository.mongo;

import java.io.Serializable;

import net.ion.framework.util.StringUtil;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PropertyId implements Serializable {

	private static final long serialVersionUID = 8480711318106901031L;

	public static enum PType implements Serializable {
		NORMAL, REFER, RESERVED
	}

	private final PType type;
	private final String name;

	private PropertyId(PType type, String name) {
		this.type = type;
		this.name = StringUtil.lowerCase(name);
	}

	public static final PropertyId normal(String key) {
		return new PropertyId(PType.NORMAL, key);
	}

	public static final PropertyId refer(String key) {
		return new PropertyId(PType.REFER, key);
	}

	public static PropertyId fromString(String idString) {
		return idString.startsWith("@") ? new PropertyId(PType.REFER, idString.substring(1)) : new PropertyId(PType.NORMAL, idString);
	}

	public String name() {
		return name;
	}

	public String fullString() {
		return ((type() == PType.REFER) ? "@" : "") + name();
	}
	
	public PType type() {
		return type;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this) ;
	}
	
	@Override
	public int hashCode(){
		return name.hashCode() + type.ordinal();
	}
	
	@Override
	public boolean equals(Object _obj){
		if (getClass().isInstance(_obj)){
			PropertyId that = (PropertyId) _obj ;
			return this.type == that.type &&  this.name.equals(that.name) ;
		}
		return false ;
	}
}
