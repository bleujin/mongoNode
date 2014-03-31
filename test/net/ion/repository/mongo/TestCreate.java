package net.ion.repository.mongo;

import net.ion.repository.mongo.node.ReadNode;
import net.ion.repository.mongo.node.WriteNode;


public class TestCreate extends TestBaseReset{

	
	public void testNewNode() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20) ;
				assertEquals(0, wnode.getLastModified()) ;
				return null;
			}
		}) ;

		ReadNode found = session.pathBy("/bleujin") ;
		assertEquals(true, found.getLastModified() > 0) ;
	}
//
//	public void testOtherType() throws Exception {
//		Node node = session.newNode() ;
//		node.put("string", "string") ;
//		node.put("sint", "99") ;
//		node.put("int", 1) ;
//		node.put("long", 1L) ;
//		node.put("boolean", true) ;
//		node.put("date", new Date()) ;
//		session.commit() ;
//		
//		
//		Debug.line(JsonParser.fromObject(node)) ;
//		
//		
//		Node found = session.createQuery().findOne() ;
//		assertEquals("string", found.get("string")) ;
//		assertEquals("99", found.get("sint")) ;
//		assertEquals(99, found.getAsInt("sint")) ;
//		assertEquals(1, found.get("int")) ;
//		assertEquals(1L, found.get("long")) ;
//		assertEquals(true, found.get("boolean")) ;
//		assertEquals(new Date().getDay(), ((Date)found.get("date")).getDay()) ;
//	}
	
	public void testCaseSentive() {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20) ;
				return null;
			}
		}) ;		

		ReadNode found = session.pathBy("/bleujin") ;
		assertEquals("bleujin", found.property("Name").asString()) ;
		assertEquals("bleujin", found.property("NAME").asString()) ;
		assertEquals("bleujin", found.property("name").asString()) ;
		assertEquals("bleujin", found.property("namE").asString()) ;

		assertEquals("bleujin", found.property("Name").asObject()) ;
		assertEquals("bleujin", found.property("NAME").asObject()) ;
		assertEquals("bleujin", found.property("name").asObject()) ;
		assertEquals("bleujin", found.property("namE").asObject()) ;

	}
	

	public void testCaseInSentiveWrite() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin").property("Name", "bleujin").property("age", 20) ;
				return null;
			}
		}) ;		
		
		assertEquals("bleujin", session.pathBy("/bleujin").property("NAME").asString()) ;
	}
	
	
	
//	public void testCreateWithPath() throws Exception {
//		session.newNode("it").put("name", "bleujin") ;
//		session.commit() ;
//		
//		assertEquals(1, session.createQuery().eq("name", "bleujin").find().count()) ;
//		assertEquals("bleujin", session.createQuery().path("/it").findOne().getString("name")) ;
//	}
//	
	

	public void testChildNode() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode bleu = wsession.pathBy("/bleu").property("Name", "bleu").property("age", 20) ;
				bleu.child("jin").property("Name", "jin").property("age", 20) ;
				return null;
			}
		}) ;
		
		assertEquals(2, session.root().children().toList().size()) ;
		
		assertEquals(20, session.pathBy("/bleu").property("age").asLong());
		assertEquals("jin", session.pathBy("/bleu/jin").property("name").asString()) ;
	}
	

//	public void testDupName() throws Exception {
//		Node newNode = session.newNode("name") ;
//		assertEquals("/name", newNode.getPath()) ;
//		
//		Node child = newNode.createChild("child") ;
//		assertEquals("/name/child", child.getPath()) ;
//		
//		try {
//			newNode.createChild("/child") ;
//			fail() ;
//		} catch(IllegalArgumentException ignore) {
//		} catch(Exception e){
//			fail() ;
//		}
//		
//		session.commit() ;
//	}
//	
//	public void testPath() throws Exception {
//		Node hello = session.newNode("hello").put("name", "bleujin") ;
//		
//		assertEquals("/hello", hello.getPath()) ;
//		session.commit() ;
//		
//		assertEquals("/hello", session.createQuery().findOne().getPath()) ;
//	}
//	
//	public void testReserved() throws Exception {
//		Node newNode = session.newNode().put("name", "bleujin") ;
//		
//		assertEquals("/" + newNode.getIdentifier(), newNode.getPath()) ;
//		assertEquals("__empty", newNode.getAradonId().getGroup()) ;
//		assertEquals(newNode.getIdentifier(), newNode.getAradonId().getUid()) ;
//	}
//
//
//	public void xtestDuplicateAradonId() throws Exception {
//		session.newNode().setAradonId("dept", "dev").put("deptno", 20);
//		session.commit() ;
//		
//		Node dupNode = session.newNode().setAradonId("dept", "dev").put("deptno", 30);
//		session.commit() ;
//		
//		NodeResult nr = session.getAttribute(NodeResult.class.getCanonicalName(), NodeResult.class) ;
//		assertEquals(true, StringUtil.isNotBlank(nr.getErrorMessage())) ;
//	}
//	
}
