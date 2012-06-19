package net.ion.radon.repository;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;

public class TestWorkspace extends TestBaseRepository{
	
	
	public void testWorkspace2() throws Exception {
		
		Node work1 = session.newNode();
		work1.put("name", "bleu");

		session.changeWorkspace(WORKSPACE_NAME + "1");
		session.dropWorkspace();
		
		Node work2 = session.newNode();
		work2.put("name", "heeya");
		
		session.commit();
		
		session.changeWorkspace(WORKSPACE_NAME);
		Node result = session.createQuery().id(work1.getIdentifier()).findOne();
		assertEquals("bleu", result.getString("name"));
		
		session.changeWorkspace(WORKSPACE_NAME + "1");
		result = session.createQuery().id(work2.getIdentifier()).findOne();
		assertEquals("heeya", result.getString("name"));
	}
	
	public void testSessionSave() throws Exception {
		
		Node newNode = session.newNode("child0") ;
		assertEquals("child0", newNode.getName()) ;
		
		session.newNode("child1") ;
		
		session.commit() ;
		assertEquals(2, session.createQuery().find().count()) ;
		
	}
	
	public void testConplict() throws Exception {
		
		session.newNode("bleujin").setAradonId("name", "bleujin");
		session.commit();
		
		session.newNode("bleujin").setAradonId("name", "bleujin") ;
		session.commit() ;
		
		NodeResult nr = session.getAttribute(NodeResult.class.getCanonicalName(), NodeResult.class) ;
		assertEquals(0, nr.getRowCount()) ;
		assertEquals(true, StringUtil.isNotBlank(nr.getErrorMessage())) ;
		assertEquals(1, session.createQuery().find().count()) ;
	}
	
	public void testWorkspace() throws Exception {
		Node newNode = session.newNode("name") ;
		Node child = newNode.createChild("child") ;
		child.append("name", "bleujin") ;

		session.changeWorkspace(WORKSPACE_NAME + "0") ;
		session.dropWorkspace();
		
		Node newNode1 = session.newNode("name") ;
		Node child1 = newNode1.createChild("child") ;
		child1.append("name", "heeya") ;
		
		session.commit();
		assertEquals(0, session.getModified().size()) ;

		session.changeWorkspace(WORKSPACE_NAME) ;
		Node load = session.createQuery().path("/name/child").findOne();
		assertEquals("bleujin", load.getString("name")) ;

		
		Debug.debug(session.getModified());
		
		// session.clear();
		
		session.changeWorkspace(WORKSPACE_NAME + "0") ;
		Node load1 =  session.createQuery().path("/name/child").findOne();
		assertEquals("heeya", load1.getString("name")) ;
		
	}

	public void testCache() throws Exception {
		Workspace one = session.changeWorkspace("123").getCurrentWorkspace() ;
		session.changeWorkspace("234") ;
		Workspace two = session.changeWorkspace("123").getCurrentWorkspace() ;
		
		assertEquals(true, one == two) ;
	}
	

	public void testWorkspaceCaseSensitiveName() throws Exception {
		
		session.changeWorkspace("MyTest") ;
		session.dropWorkspace() ;
		
		session.newNode().put("name", "bleujin") ;
		session.commit() ;
		
		assertEquals(1, session.getWorkspace("mytest").count()) ;
	}
	
	
	
	
}
