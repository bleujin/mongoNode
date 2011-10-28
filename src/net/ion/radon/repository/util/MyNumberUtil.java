package net.ion.radon.repository.util;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

public class MyNumberUtil extends NumberUtil{

	public static int getAsInt(Object value){
		if (value instanceof Double) {
			return ((Double)value).intValue() ; 
		}
		
		final String str = StringUtil.toString(value);
		return isNumber(str) ? Integer.parseInt(str) : 0;
	}

}
