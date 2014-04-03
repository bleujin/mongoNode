package net.ion.repository.mongo.node;

import net.ion.framework.util.StringUtil;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.WriteJobs;

public class TestUpdate extends TestBaseReset {

	public void testModifyField() throws Exception {
		session.tranSync(WriteJobs.HELLO);
		assertEquals("hello", session.pathBy("/bleujin").property("greeting").asString());

		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("greeting", "hi");
				return null;
			}
		});

		assertEquals("hi", session.pathBy("/bleujin").property("greeting").asString());
	}

	public void testImplictAddField() throws Exception {
		session.tranSync(WriteJobs.HELLO);

		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("location", "seoul");
				return null;
			}
		});

		assertEquals("bleujin", session.pathBy("/bleujin").property("name").asString());
		assertEquals("seoul", session.pathBy("/bleujin").property("location").asString());
		assertEquals("hello", session.pathBy("/bleujin").property("greeting").asString());
	}

	public void testResult() throws Exception {
		session.tranSync(WriteJobs.HELLO);

		assertEquals(true, session.attribute(NodeResult.class.getCanonicalName(), NodeResult.class).getRowCount() > 0);
		NodeResult nresult = session.attribute(NodeResult.class.getCanonicalName(), NodeResult.class);
		assertTrue(nresult.getRowCount() >= 0);
		assertTrue(StringUtil.isBlank(nresult.errorMessage()));
	}

	
	public void testWriteNodeInScope() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode f1 = wsession.pathBy("/bleujin").property("name", "bleujin") ;
				WriteNode f2 = wsession.pathBy("/bleujin").property("age", 20) ;
				
				assertEquals(true, f1 == f2);
				return null;
			}
		}) ;
		
		assertEquals("bleujin", session.pathBy("/bleujin").property("name").asString());
		assertEquals(20, session.pathBy("/bleujin").property("age").asInt());
		
	}
	
	
	public void testApplyLastModifiedWhenModified() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;
		
		long created = session.pathBy("/bleujin").getLastModified() ;
		Thread.sleep(10);
		session.tranSync(WriteJobs.HELLO) ;
		long modified = session.pathBy("/bleujin").getLastModified() ;
		
		assertEquals(true, created < modified);
	}
	
	

	public void testClear() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20).append("color", "red", "blue", "white", "red") ;
				return null;
			}
		}) ;
		
		ReadNode found = session.pathBy("/bleujin") ;
		assertEquals("bleujin", found.property("name").asString());
		assertEquals(20, found.property("age").asInt());
		assertEquals("red", found.property("color").asString());
		
		
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").clear() ;
				return null;
			}
		}) ;

		found = session.pathBy("/bleujin") ;
		assertEquals(0, found.propSize());
		assertEquals(0, found.normalKeys().size()) ;
	}
	
	
	public void testUnSet() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20).append("color", "red", "blue", "white", "red") ;
				return null;
			}
		}) ;
		
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").unset("name").unset("color", "red", "blue") ;
				return null;
			}
		}) ;
		
		assertEquals(true, session.pathBy("/bleujin").property("name") == PropertyValue.NotFound);
		assertEquals("white", session.pathBy("/bleujin").property("color").asString());
		assertEquals(1, session.pathBy("/bleujin").property("color").asSet().size());
	}
	
	
	// public void testMultiUpdate() throws Exception {
	// // update workspace set location = 'seoul' where name = 'bleujin'
	//
	// createTestNode() ;
	// createTestNode() ;
	//
	// NodeResult result = session.createQuery().eq("name", "bleujin").update(MapUtil.chainMap().put("location", "seoul")) ;
	//
	// assertEquals(true, result.getErrorMessage() == null);
	//
	// NodeCursor cursor = createQuery().eq("name", "bleujin").find();
	// cursor.each(PageBean.ALL, new Closure<Node>(){
	// public void execute(Node node) {
	// assertEquals("bleujin", node.getString("name"));
	// assertEquals("seoul", node.getString("location"));
	// }
	//
	// });
	// }
}