package net.ion.radon.repository;

import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;

import org.apache.commons.collections.Closure;

public class TestUpdate extends TestBaseRepository{

	public void testUpdate() throws Exception {
		
		Node node = createTestNode();
		Node find = session.createQuery().eq("name", "bleujin").findOne() ;
		find.put("name", "hero") ;
		
		session.commit() ;
		
		// confirm
		session.logout() ;
		Session newSession = rc.testLogin("test", WORKSPACE_NAME) ;
		
		session.createQuery().find().debugPrint(PageBean.ALL) ;
		
		assertEquals("hero", newSession.createQuery().id(node.getIdentifier()).findOne().getString("name")) ;
	}


	private Node createTestNode() {
		Node node = session.newNode() ;
		node.put("name", "bleujin") ;
		node.put("greeting", "hello") ;
		
		session.commit() ;
		return node;
	}
	
	
	public void testFindOverwrite() throws Exception {
		Node savedNode = createTestNode() ;
		
		Map<String, String> map = MapUtil.create("name", "hero") ;
		
		boolean result = session.createQuery().eq("name", "bleujin").overwriteOne(map) ;
		// confirm
		session.logout() ;
		Session newSession = rc.testLogin("test", WORKSPACE_NAME) ;
		Node found = newSession.createQuery().id(savedNode.getIdentifier()).findOne();
		assertEquals("hero", found.getString("name")) ;
		assertTrue(found.getString("greeting") == null) ;
	}
	

	public void testFindUpdate() throws Exception {
		Node savedNode = createTestNode() ;
		
		Map map = MapUtil.create("location", "seoul") ;
		
		session.createQuery().eq("name", "bleujin").updateOne(map) ;
		// confirm
		session.logout() ;
		Session newSession = rc.testLogin("test", WORKSPACE_NAME) ;
		assertEquals("bleujin", newSession.createQuery().id(savedNode.getIdentifier()).findOne().getString("name")) ;
		assertEquals("seoul", newSession.createQuery().id(savedNode.getIdentifier()).findOne().getString("location")) ;	
	}

	public void testFindMultiUpdate() throws Exception {
		Node savedNode = createTestNode() ;
		
		session.createQuery().eq("name", "bleujin").put("location", "seoul").update() ;
		// confirm
		session.logout() ;
		Session newSession = rc.testLogin("test", WORKSPACE_NAME) ;
		assertEquals("bleujin", newSession.createQuery().id(savedNode.getIdentifier()).findOne().getString("name")) ;
		assertEquals("seoul", newSession.createQuery().id(savedNode.getIdentifier()).findOne().getString("location")) ;	
	}

	
	public void testNotFindUpdate() throws Exception {
		Node savedNode = createTestNode() ;
		
		Map map = MapUtil.create("location", "seoul") ;
		
		boolean result = session.createQuery().eq("name", "newname").updateOne(map) ;
		assertEquals(false, result) ;
	}
	

	
	public void testMultiUpdate() throws Exception {
		// update workspace set location = 'seoul' where name = 'bleujin'
		
		createTestNode() ;
		createTestNode() ;
		
		NodeResult result = session.createQuery().eq("name", "bleujin").put("location", "seoul").update() ;
		
		assertEquals(true, result.getErrorMessage() == null);
		
		NodeCursor cursor =  createQuery().eq("name", "bleujin").find();
		cursor.each(PageBean.ALL, new Closure(){
			public void execute(Object obj) {
				Node node = (Node) obj;
				assertEquals("seoul", node.getString("location"));
			}
			
		});
	}
	
	public void testAfterUpdate() throws Exception {
		testMultiUpdate() ;
		
		NodeResult results = session.getLastResultInfo() ;
		assertEquals(true, results.getRowCount() > 0); 
	}
	
	public void testRemove() throws Exception {
		createTestNode() ;
		createTestNode() ;
		
		Node node = session.newNode();
		node.put("name", "heeya");
		session.commit();

		int result = session.createQuery().eq("name", "bleujin").remove();
		assertEquals(2, result);
		assertEquals(1, session.createQuery().find().count());
		
	}
	
	public void testAdReference() throws Exception {
		Node node1 = createTestNode() ;
		session.changeWorkspace(WORKSPACE_NAME2) ;
		session.dropWorkspace() ;
		Node node2 = createTestNode() ;
		
		session.addReference(node1, "friend", node2) ;
		
		session.commit();
		ReferenceTaragetCursor rc = session.createRefQuery().from(node1).find();
		assertEquals(1, rc.count()) ;
	}
	
	public void testReferenceRemove() throws Exception {
		Node node1 = createTestNode() ;
		Node node2 = createTestNode() ;
		
		session.addReference(node1, "ref", node2);
		
		session.commit() ;
		ReferenceTaragetCursor rc = session.createRefQuery().from(node1).find() ;
		assertEquals(1, rc.count()) ;
		
		NodeResult nr = session.createRefQuery().from(node1, "ref").remove();
		Debug.debug(nr) ;
		
		
		session.commit() ;
		rc = session.createRefQuery().from(node1).find();
		assertEquals(0, rc.count()) ;
	}	

	
	
	
	
	
}
