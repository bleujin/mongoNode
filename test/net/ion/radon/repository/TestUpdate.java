package net.ion.radon.repository;

import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;

import org.apache.commons.collections.Closure;

public class TestUpdate extends TestBaseRepository{

	private Node createTestNode() {
		Node node = session.newNode() ;
		node.put("name", "bleujin") ;
		node.put("greeting", "hello") ;
		
		session.commit() ;
		return node;
	}

	
	public void testUpdate() throws Exception {
		createTestNode();
		
		Node find = session.createQuery().eq("name", "bleujin").findOne() ;
		find.put("greeting", "hi") ;
		
		session.commit() ; // must
		
		assertEquals("hi", session.createQuery().eq("name", "bleujin").findOne().getString("greeting")) ;
	}


	public void testFindUpdate() throws Exception {
		Node savedNode = createTestNode() ;
		
		Map map = MapUtil.create("location", "seoul") ;
		session.createQuery().eq("name", "bleujin").updateOne(map) ;
		; // no commit
		
		assertEquals("bleujin", session.createQuery().id(savedNode.getIdentifier()).findOne().getString("name")) ;
		assertEquals("seoul", session.createQuery().id(savedNode.getIdentifier()).findOne().getString("location")) ;	
	}


	public void testFindOverwrite() throws Exception {
		Node savedNode = createTestNode() ;
		
		Map<String, String> map = MapUtil.create("name", "hero") ;
		session.createQuery().eq("name", "bleujin").overwriteOne(map) ;
		
		Node found = session.createQuery().id(savedNode.getIdentifier()).findOne();
		assertEquals("hero", found.getString("name")) ;
		assertTrue(found.getString("greeting") == null) ;
	}
	
	
	public void testNotExistUpdate() throws Exception {
		createTestNode() ;
		
		boolean result = session.createQuery().eq("name", "notfound").updateOne(MapUtil.create("location", "seoul")) ;
		assertEquals(false, result) ;
	}

	public void testMultiUpdate() throws Exception {
		// update workspace set location = 'seoul' where name = 'bleujin'
		
		createTestNode() ;
		createTestNode() ;
		
		NodeResult result = session.createQuery().eq("name", "bleujin").update(MapUtil.chainMap().put("location", "seoul")) ;
		
		assertEquals(true, result.getErrorMessage() == null);
		
		NodeCursor cursor =  createQuery().eq("name", "bleujin").find();
		cursor.each(PageBean.ALL, new Closure(){
			public void execute(Object obj) {
				Node node = (Node) obj;
				assertEquals("bleujin", node.getString("name"));
				assertEquals("seoul", node.getString("location"));
			}
			
		});
	}
	
	public void testResult() throws Exception {
		testMultiUpdate() ;
		
		assertEquals(true, session.getLastResultInfo().getRowCount() > 0); 
	}
	

	public void testLastResult() throws Exception {
		session.newNode("name");
		session.commit();
		
		NodeResult nresult = session.getLastResultInfo();
		assertTrue(nresult.getRowCount() >= 0) ;
		assertTrue(StringUtil.isBlank(nresult.getErrorMessage())) ;
	}

}
