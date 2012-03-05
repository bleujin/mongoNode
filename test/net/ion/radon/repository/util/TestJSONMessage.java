package net.ion.radon.repository.util;

import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class TestJSONMessage extends TestCase{

	
	public void testLoad() throws Exception {
		JSONMessage mp = JSONMessage.create().put("name", "bleujin").inner("address").put("city", "seoul").toRoot().inner("phone").put("first", 1234).put("sec", "0") ;
		
		assertEquals("bleujin", mp.getString("name")) ;
		assertEquals("seoul", mp.getString("address.city")) ;
		assertEquals(1234, mp.get("phone.first")) ;
	}
	
	public void testToJSON() throws Exception {
		JSONMessage mp = JSONMessage.create().put("name", "bleujin").inner("address").put("city", "seoul").toRoot().inner("phone").put("first", 1234).put("sec", "0") ;
		
		Map map = mp.toJSON().toMap() ;

		int index = 0 ;
		for (Object _entry : map.entrySet()) {
			Entry entry = (Entry)_entry ;
			index++ ;
			Debug.line(entry) ;
		}
		assertEquals(3, index) ;
		
		
	}
}
