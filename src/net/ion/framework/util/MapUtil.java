package net.ion.framework.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtil {
	public final static Map EMPTY = Collections.EMPTY_MAP ;
	
	public final static <K, V> Map<K, V> newMap(){
		return new HashMap<K, V>() ;
	}

	public final static <K, V> Map<K, V> newSyncMap(){
		return Collections.synchronizedMap(new HashMap<K, V>()) ;
	}


	public final static <V> Map<String, V> newCaseInsensitiveMap(){
		return new CaseInsensitiveHashMap<V>() ;
	}

	public final static <K, V> Map<K, V> newOrdereddMap(){
		return new LinkedHashMap<K, V>() ;
	}
	
	public static<K, T> Map<K, T> create(K key, T value) {
		Map<K, T> result = new HashMap<K, T>() ;
		result.put(key, value) ;
		return result;
	}


	public static Map<String, Object> stringMap(String key, Object value) {
		Map<String, Object> result = new HashMap<String, Object>() ;
		result.put(key, value) ;
		return result;
	}	
	
	public static <K, V> ChainMap<K, V> chainMap(){
		return new ChainMap<K, V>() ;
	}

	public static <V> ChainMap<String, V> chainKeyMap(){
		return new ChainMap<String, V>() ;
	}
	
}
