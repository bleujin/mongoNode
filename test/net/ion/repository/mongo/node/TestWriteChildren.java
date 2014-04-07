package net.ion.repository.mongo.node;

import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.WriteJobs;

public class TestWriteChildren extends TestBaseReset{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		session.tranSync(WriteJobs.dummy("/bleujin", 10)) ;
	}
	
	public void testWriteChildren() throws Exception {
		
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteChildren children = wsession.pathBy("/bleujin").children() ;
				assertEquals(10, children.count()) ;
				return null;
			}
		}) ;
	}

	
	public void testFindOne() throws Exception {
		ReadNode foundNode = session.tranSync(new WriteJob<ReadNode>() {
			@Override
			public ReadNode handle(WriteSession wsession) {
				WriteNode found = wsession.pathBy("/bleujin").children().between("dummy", 3, 5).descending("dummy").firstNode() ;
				assertEquals(5, found.property("dummy").asInt());
				
				found.property("dummy", -1) ;
				return found.readNode();
			}
		}) ;
		
		assertEquals(-1, foundNode.property("dummY").asInt());
	}
	
	
	public void testRemove() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").children().between("dummy", 3, 5).remove() ;
				
				return null;
			}
		}) ;
		
		assertEquals(0, session.pathBy("/bleujin").children().between("dummy", 3, 5).count()) ;
	}
	
	public void testFindUpdate() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").children().between("DUMMY", 3, 5).property("mod", 10).findUpdate() ;
				return null;
			}
		}) ;
		assertEquals(3, session.pathBy("/bleujin").children().eq("mod", 10).count()) ;
		
		ReadNode findOne = session.pathBy("/bleujin").children().eq("mod", 10).descending("dummy").firstNode() ;
		assertEquals(5, findOne.property("dummy").asInt());
		assertEquals(10, findOne.property("mod").asInt());
		assertEquals("dummy", findOne.property("name").asString());
	}
	
	
	
	
	public void testRefTo() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/depts/dev").property("name", "develop") ;
				wsession.pathBy("/bleujin").children().between("DUMMY", 3, 5).refTo("dept", "/depts/dev").findUpdate() ;
				return null;
			}
		}) ;
		
		session.pathBy("/bleujin").children().between("dummy", 3, 5).eachNode(new ReadChildrenEach<Void>() {
			@Override
			public Void handle(ReadChildrenIterator citer) {
				while(citer.hasNext()){
					ReadNode next = citer.next() ;
					assertEquals("develop", next.ref("dept").property("name").asString()) ;
				}
				return null;
			}
		}) ;
	}

	
	public void testUnSet() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").children().between("DUMMY", 3, 5).property("age", 20).unset("name").findUpdate() ;
				return null;
			}
		}) ;
		
		session.pathBy("/bleujin").children().between("dummy", 3, 5).eachNode(new ReadChildrenEach<Void>() {
			@Override
			public Void handle(ReadChildrenIterator citer) {
				while(citer.hasNext()){
					ReadNode next = citer.next() ;
					assertEquals(true, next.property("name") == PropertyValue.NotFound) ;
					assertEquals(20, next.property("age").asInt()) ;
				}
				return null;
			}
		}) ;
//		session.collection().debugPrint(); 
	}
	
	public void testEachNode() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				WriteChildren children = wsession.pathBy("/bleujin").children().between("dummy", 3, 5) ;
				children.eachNode(new WriteChildrenEach<Void>() {
					@Override
					public Void handle(WriteChildrenIterator citer) {
						while(citer.hasNext()){
							citer.next().property("age", 20).append("address", "seoul", "busan").append("name", "jin") ;
						}
						return null;
					}
				}) ;
				return null;
			}
		}) ;
		session.pathBy("/bleujin").children().between("dummy", 3, 5).eachNode(new ReadChildrenEach<Void>() {
			@Override
			public Void handle(ReadChildrenIterator citer) {
				while(citer.hasNext()){
					ReadNode next = citer.next() ;
					assertEquals(2, next.property("name").asSet().size()) ;
					assertEquals("seoul", next.property("address").asString()) ;
					assertEquals(2, next.property("address").asSet().size()) ;
				}
				return null;
			}
		}) ;
	
//		session.collection().debugPrint(); 
	}
	
	
	public void testIncrease() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").children().between("dummy", 3, 5).increase("dummy", 10).findUpdate(); 
				return null;
			}
		}) ;
		
		assertEquals(3, session.pathBy("/bleujin").children().gt("dummy", 10).count()) ;
	}
	
	
	public void testFindById() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").children().eq("_id", "/bleujin/3").increase("dummy", 10).findUpdate(); 
				return null;
			}
		}) ;
		
		assertEquals(13, session.pathBy("/bleujin/3").property("dummy").asInt()) ;
	}
	
	
	
	
}
