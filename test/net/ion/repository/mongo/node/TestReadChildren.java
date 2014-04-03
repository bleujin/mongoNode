package net.ion.repository.mongo.node;


import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.ReadChildrenEachs;
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

	
	public void xtestChildren() throws Exception {
		session.pathBy("/bleujin").children().debugPrint() ;
	}
	
	public void testChildrenCount() throws Exception {
		assertEquals(1, session.root().children().count());
		assertEquals(10, session.pathBy("/bleujin").children().count());
	}
	
	
	public void testFilter() throws Exception {
		assertEquals(1, session.pathBy("/bleujin").children().eq("dummy", 3).toList().size()) ;
		assertEquals(3, session.pathBy("/bleujin").children().gte("dummy", 7).toList().size()) ;
		assertEquals(2, session.pathBy("/bleujin").children().gt("dummy", 7).toList().size()) ;
		assertEquals(4, session.pathBy("/bleujin").children().lte("dummy", 3).toList().size()) ;
		assertEquals(3, session.pathBy("/bleujin").children().lt("dummy", 3).toList().size()) ;
		assertEquals(3, session.pathBy("/bleujin").children().between("dummy", 3, 5).toList().size()) ;

		assertEquals(3, session.pathBy("/bleujin").children().in("dummy", 3, 4, 5, 10).toList().size()) ;
	}
	
	
	public void testIncludeSubPath() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").child("address/city").property("name", "seoul") ;
				wsession.ensureIndex("name_idx").ascending("name").create(); 
				return null;
			}
		}) ;
		
		int count = session.root().children(true).eq("name", "seoul").eachNode(new ReadChildrenEach<Integer>() {
			@Override
			public Integer handle(ReadChildrenIterator citer) {
				Explain explain = citer.explain() ;
				assertEquals(true, explain.useIndex()) ;
				assertEquals("name_idx", explain.useIndexName()) ;
				
				return citer.count();
			}
		}) ;
		
		assertEquals(true, count == 1) ;
	}
	
	
	public void testExplainWhenRead() throws Exception {
		createHelloNode();

		assertEquals("bleujin", session.pathBy("/bleujin").property("name").asString());

		Explain explain = session.pathBy("/bleujin").children().eachNode(new ReadChildrenEach<Explain>() {
			@Override
			public Explain handle(ReadChildrenIterator citer) {
				return citer.explain();
			}
		});

		assertEquals(false, explain.useIndex());
	}
	
	
	
}
