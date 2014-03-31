package net.ion.repository.mongo;

import java.io.Serializable;

import net.ion.framework.util.StringUtil;

public class PropertyId implements Serializable {

	private static final long serialVersionUID = 8480711318106901031L;

	public static enum PType implements Serializable {
		NORMAL, REFER
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

	public String idString() {
		return name;
	}

	public PType type() {
		return type;
	}
}
