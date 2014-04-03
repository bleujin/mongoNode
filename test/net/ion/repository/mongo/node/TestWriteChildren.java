package net.ion.repository.mongo.node;

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
				wsession.pathBy("/bleujin").children().between("dummy", 3, 5).property("mod", 10).findUpdate() ;
				return null;
			}
		}) ;
		assertEquals(3, session.pathBy("/bleujin").children().eq("mod", 10).count()) ;
		
		ReadNode findOne = session.pathBy("/bleujin").children().eq("mod", 10).descending("dummy").findOne() ;
		assertEquals(5, findOne.property("dummy").asInt());
		assertEquals(10, findOne.property("mod").asInt());
		assertEquals("dummy", findOne.property("name").asString());
	}
	
	
}
