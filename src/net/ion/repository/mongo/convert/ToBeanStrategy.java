package net.ion.repository.mongo.convert;

import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.MapUtil;
import net.ion.repository.mongo.node.ReadNode;
import net.ion.repository.mongo.util.Transformers;

public interface ToBeanStrategy {
	
	public final static ToBeanStrategy EasyByJson = new JsonStrategy() ;
	public final static ToBeanStrategy ProxyByCGLib = new ProxyByCGLib() ;
	
	public <T> T toBean(ReadNode node, Class<T> clz) ;
}

class ProxyByCGLib implements ToBeanStrategy {

	@Override
	public <T> T toBean(ReadNode node, Class<T> clz) {
		return ProxyBean.create(TypeStrategy.DEFAULT, node, clz) ;
	}
	
}


class JsonStrategy implements ToBeanStrategy {

	@Override
	public <T> T toBean(ReadNode node, Class<T> clz) {
		final Map<String, Object> map = removeKeyPrefix(node.transformer(Transformers.READ_TOFLATMAP));
		return JsonParser.fromObject(map).getAsJsonObject().getAsObject(clz) ;
	}
	
	private Map<String, Object> removeKeyPrefix(Map<String, Object> inner){
		Map<String, Object> result = MapUtil.newMap() ;
		
		for (Entry<String, Object> entry : inner.entrySet()) {
			String modKey = (entry.getKey().startsWith("#") || entry.getKey().startsWith("@")) ? entry.getKey().substring(1) : entry.getKey() ;
			result.put(modKey, (entry.getValue() instanceof Map) ? removeKeyPrefix((Map<String, Object>)entry.getValue()) : entry.getValue()) ;
		}
		
		return result ;
	}

}

