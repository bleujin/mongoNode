package net.ion.radon.repository;

import java.util.List;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;

import org.bson.types.ObjectId;

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
	
	
	
	public void testReference() throws Exception {
		
		Node newNode = session.newNode("new") ;
		
		Node hero = session.newNode("hero") ;
		hero.setAradonId("test", "hero") ;
		hero.put("name", "hero") ;
		hero.put("address", "busan") ;
        
		session.addReference(newNode, "friend", hero) ;
		
		session.commit();

		newNode = session.createQuery().id(newNode.getIdentifier()).findOne() ;
		
		final ReferenceTaragetCursor findReference = session.createRefQuery().from(newNode).find();
		Debug.debug(findReference.size()) ;
		List<Node> nodes = findReference.toList(PageBean.ALL) ;
		
	}
	
	
	public void testSessionSave() throws Exception {
		
		Node newNode = session.newNode("child0") ;
		assertEquals("child0", newNode.getName()) ;
		
		session.newNode("child1") ;
		
		session.commit() ;
		assertEquals(2, session.createQuery().find().count()) ;
		
	}
	
	public void testConplict() throws Exception {
		
		session.newNode("bleujin").put("name", "bleujin");
		session.commit();
		
		try{
			session.newNode("bleujin").put("name", "hero") ;
			int count = session.commit() ;
			
			fail();
		}catch(RepositoryException e){
		}
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
		Node load = session.createQuery().findByPath("/name/child");
		assertEquals("bleujin", load.getString("name")) ;

		
		Debug.debug(session.getModified());
		
		// session.clear();
		
		session.changeWorkspace(WORKSPACE_NAME + "0") ;
		Node load1 =  session.createQuery().findByPath("/name/child");
		assertEquals("heeya", load1.getString("name")) ;
		
	}
	

	
	
	
	
}
