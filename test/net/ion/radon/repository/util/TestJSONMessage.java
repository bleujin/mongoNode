package net.ion.radon.repository.util;

import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestJSONMessage extends TestCase{

	
	public void testLoad() throws Exception {
		JSONMessage mp = JSONMessage.create().put("name", "bleujin").inner("address").put("city", "seoul").toRoot().inner("phone").put("first", 1234).put("sec", "0") ;
		
		Debug.debug(mp.toString(), mp.toJSON()) ;
		
		assertEquals("bleujin", mp.getString("name")) ;
		assertEquals("seoul", mp.getString("address.city")) ;
		assertEquals(1234, mp.get("phone.first")) ;
	}
	
	public void testToJSON() throws Exception {
		JSONMessage mp = JSONMessage.create().put("name", "bleujin").inner("address").put("city", "seoul").toRoot().inner("phone").put("first", 1234).put("sec", "0") ;
		
		Map map = mp.toJSON() ;
		
		
		for (Object _entry : map.entrySet()) {
			Entry entry = (Entry)_entry ;
			Debug.line(entry.getKey(), entry.getValue()) ;
		}
		
		
	}
}
