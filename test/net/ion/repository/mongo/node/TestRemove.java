package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.WriteJobs;

public class TestRemove extends TestBaseReset {

	public void testRemoveChild() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;
		
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().removeChild("bleujin") ;
				return null;
			}
		}) ;
		
		assertEquals(false, session.exists("/bleujin"));
	}

	public void testRemoveChildren() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").child("address").property("city","seoul") ;
				return null;
			}
		}) ;
		
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").removeChildren(); 
				return null;
			}
		}) ;
		
		assertEquals(false, session.exists("/bleujin/address")) ;
		assertEquals(true, session.exists("/bleujin")) ;
	}
	
	
	public void testRemoveChildrenRootSub() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().removeChildren();
				return null;
			}
		}) ;

		assertEquals(false, session.exists("/bleujin"));
	}

	public void testRemoveChildrenWithDecendant() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").child("address").property("city", "seoul") ;
				return null;
			}
		}) ;
		
		assertEquals(true, session.exists("/bleujin")) ;
		assertEquals(true, session.exists("/bleujin/address")) ;
		
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().removeChildren(); 
				return null;
			}
		}) ;
		
		assertEquals(false, session.exists("/bleujin")) ;
		assertEquals(false, session.exists("/bleujin/address")) ;
	}

	
}
