package net.ion.repository.mongo.node;

import java.util.Map;

import com.mongodb.DBObject;

import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestToMap extends TestBaseReset{


	public void testToMap() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("color", 128).child("address").property("city", "seoul") ;
				return null;
			}
		}) ;
		
		ReadNode found = session.pathBy("/bleujin") ;
		Map<PropertyId, PropertyValue> map = found.toMap() ;
		assertEquals("bleujin", map.get(PropertyId.fromString("name")).asString());
		
	}
		
//	public void testGet() throws Exception {
//		INode node = session.tempNode().put("name", "bleujin").append("color", 128).append("color", 134).inner("address").put("city", "seoul").getParent() ;
//		
//		assertEquals(134, node.get("color", 1));
//		assertEquals("seoul", node.get("address.city"));
//	}
//	
//	public void testInList() throws Exception {
//		TempNode node = session.tempNode();
//		InListNode in = node.put("name", "bleujin").append("color", 128).append("color", 134).inlist("people") ;
//		for (int i = 0; i < 5 ; i++) {
//			in.push(MapUtil.chainMap().put("index", 1).put("name", "hero")) ;
//		}
//		assertEquals(true, node.get("people", 1) instanceof InNode); 
//		assertEquals(true, node.get("color") instanceof InListNode) ;
//		
//		assertEquals(128, node.get("color", 0)) ;
//	}
}
