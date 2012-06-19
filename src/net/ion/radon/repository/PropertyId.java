package net.ion.radon.repository;

import net.ion.framework.util.StringUtil;

import org.apache.commons.lang.CharUtils;

public class PropertyId {

	private String innerKey;

	private PropertyId(String key) {
		this.innerKey = key.toLowerCase();
	}

	public static PropertyId create(String key) {
		checkAllowedPropertyName(key);
		return new PropertyId(key);
	}

	private static void checkAllowedPropertyName(String pkey) {
		if (pkey.startsWith("__") && StringUtil.isSmallAlphaNumUnderBar(pkey)) {
			return ; // allow . 
		}
		if (StringUtil.isBlank(pkey) || NodeUtil.isReservedProperty(pkey)) {
			// if (StringUtil.isBlank(pkey)) {
			throw new IllegalArgumentException("illegal property id :" + pkey);
		}
//		if (CharUtils.isAsciiNumeric(pkey.charAt(0)))
//			throw new IllegalArgumentException("illegal property id :" + pkey);
	}

	public String getKeyString() {
		return innerKey;
	}

	public static PropertyId reserved(String key) {
		if (!key.startsWith("_"))
			throw new IllegalArgumentException("illegal reserved property id :" + key);

		return new PropertyId(key);
	}
}