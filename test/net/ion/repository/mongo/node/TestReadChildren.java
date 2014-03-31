package net.ion.repository.mongo.node;


import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.util.WriteJobs;

public class TestReadChildren extends TestBaseReset{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		session.tranSync(WriteJobs.dummy("/bleujin", 10)) ;
	}
	
	public void testParent() throws Exception {
		assertEquals(true, session.exists("/bleujin")) ;
		assertEquals("/", session.pathBy("/bleujin").property("_parent").asString()) ;
	}

	
	public void testChildren() throws Exception {
		session.pathBy("/bleujin").children().debugPrint() ;
	}
	
	public void testFilter() throws Exception {
		assertEquals(1, session.pathBy("/bleujin").children().eq("dummy", 3).toList().size()) ;
		assertEquals(3, session.pathBy("/bleujin").children().gte("dummy", 7).toList().size()) ;
		assertEquals(3, session.pathBy("/bleujin").children().between("dummy", 3, 5).toList().size()) ;
	}
}
