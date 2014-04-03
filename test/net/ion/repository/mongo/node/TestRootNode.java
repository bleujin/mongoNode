package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestRootNode extends TestBaseReset {

	public void testAlwaysExist() throws Exception {
		ReadNode root = session.root();

		assertEquals("", root.fqn().name());
		assertEquals("/", root.fqn().toString());
	}
	
	public void testChild() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").child("child") ;
				return null;
			}
		}) ;

		ReadNode bleujin = session.pathBy("/bleujin") ;
		
		assertEquals(true, bleujin.parent().equals(session.root()));
		assertEquals(1, session.root().children().toList().size());
		assertEquals(1, session.pathBy("/bleujin").children().toList().size());
	}

	public void testPath() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode newNode = wsession.pathBy("/bleujin") ;
				assertEquals("/bleujin", newNode.fqn().toString());
				return null;
			}
		}) ;
	}
	
	public void testRootParentIsRoot() throws Exception {
		assertEquals(true, session.root().fqn().isRoot()) ;
		assertEquals(true, session.root().parent().fqn().isRoot()) ;
	}
	
	
	public void testRootIsNode() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().property("name", "root") ;
				return null;
			}
		}) ;
		
		
		assertEquals("root", session.root().property("name").asString()) ;
		
	}
	
}

