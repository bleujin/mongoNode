package net.ion.repository.mongo.node;

import java.util.Map;

import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.Transformers;

public class TestToPropMap extends TestBaseReset{


	public void testToPropMap() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("color", 128).child("address").property("city", "seoul") ;
				return null;
			}
		}) ;
		
		ReadNode found = session.pathBy("/bleujin") ;
		Map<PropertyId, PropertyValue> map = found.toPropMap() ;
		assertEquals("bleujin", map.get(PropertyId.fromString("name")).asString());
		assertEquals(2, map.size());
	}
		
	
	public void testToMapWithRelation() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").append("color", "red", "white").property("city", "seoul").refTo("friend", "/hero") ;
				return null;
			}
		}) ;
		ReadNode found = session.pathBy("/bleujin") ;
		Map<PropertyId, PropertyValue> map = found.toPropMap() ;
		assertEquals("bleujin", map.get(PropertyId.fromString("name")).asString());
		assertEquals("red", map.get(PropertyId.fromString("color")).asString());
		assertEquals(2, map.get(PropertyId.fromString("color")).asSet().size());
		assertEquals(3, map.size());
		
		
	}
	
	public void testFlatMap() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").append("color", "red", "white").property("city", "seoul").refTo("friend", "/hero") ;
				return null;
			}
		}) ;
		ReadNode found = session.pathBy("/bleujin") ;
		
		Map<String, Object> flatMap = found.transformer(Transformers.READ_TOFLATMAP) ;
		assertEquals(3, flatMap.size());
		assertEquals("bleujin", flatMap.get("name"));
		assertEquals("red", flatMap.get("color"));
		assertEquals("seoul", flatMap.get("city"));
	}
	
	
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
