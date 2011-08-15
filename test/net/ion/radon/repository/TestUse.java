package net.ion.radon.repository;

import java.util.List;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;

import org.bson.types.ObjectId;

import com.mongodb.Mongo;

import junit.framework.TestCase;

public class TestUse extends TestBaseRepository{
	
	public void testNewNode() throws Exception {
		
		Node parent = session.newNode() ;
		parent.put("name", "bleu");
		
		Node child = parent.createChild("jin") ;
		session.commit();
		
		Node result = session.createQuery().id(parent.getIdentifier()).findOne();
		assertEquals("bleu", result.get("name"));
	}
	
	public void testWorkspace() throws Exception {
		
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
	
	
	public void testSame() throws Exception {
		Node bleujin = session.newNode("bleujin");
		bleujin.put("name", "bleu");
		
		Node load = session.createQuery().findByPath("/bleujin") ;
		assertTrue(bleujin == load) ;

		// assertTrue(bleujin == session.createQuery().id(bleujin.getIdentifier()).findOne()) ;
	}
	
	
	public void testReference() throws Exception {
		Node work1 = session.newNode();
		work1.put("name", "bleu");
		
		Node child1 = work1.createChild("child1");
		Node child2 = work1.createChild("child2");
		
		session.addReference(child1, "friend", child2);
		session.commit();
		
		Node result = session.createQuery().id(child1.getIdentifier()).findOne();
		
		final ReferenceTaragetCursor findReference = session.createRefQuery().from(result).find();
		assertEquals(1, findReference.size());
	}
	
	
	public void testParentChild() throws Exception {
		Node work1 = session.newNode();
		work1.put("name", "bleu");
		
		Node child1 = work1.createChild("child1");
		Node child2 = work1.createChild("child2");

		
		assertEquals(5, session.getModified().size()) ;

		session.commit() ;
		
		
		Node parent = child1.getParent() ;
		assertEquals("bleu", parent.getString("name")) ;
		
		assertTrue(session.getRoot() == work1.getParent()) ;
	}
	
	public void testParentChild2() throws Exception {
		Node work1 = session.newNode();
		work1.put("name", "bleu");
		
		assertTrue(session == work1.getSession());
	}
	
	public void testConplict() throws Exception {
		session.newNode("name");
		session.commit();
		try{
			session.newNode("name") ;
			fail();
		}catch(RepositoryException e){
		}
	}
	
	
	public void testLastResult() throws Exception {
		session.newNode("name");
		session.commit();
		
		assertTrue(session.getLastResultInfo().get(0).getRowCount() >= 0) ;
		Debug.debug(session.getLastResultInfo().get(0).isLazy()) ;
	}
	
	public void testSetName() throws Exception {
		
		Node newNode = session.newNode();
		newNode.put("name", "bleujin") ;
		
		assertEquals("/" + newNode.getIdentifier(), newNode.getPath()) ;
		

		Node hello = session.newNode("hello");
		hello.put("name", "bleujin") ;
		assertEquals("/hello", hello.getPath()) ;
		
	}
	
	
	
	public void testCaseSentive() {
		Node newNode = session.newNode();
		newNode.put("nAme", "bleujin") ;

		assertEquals("bleujin", newNode.getString("Name")) ;
		assertEquals("bleujin", newNode.getString("NAME")) ;
		assertEquals("bleujin", newNode.getString("name")) ;
		assertEquals("bleujin", newNode.getString("namE")) ;

		assertEquals("bleujin", newNode.get("Name")) ;
		assertEquals("bleujin", newNode.get("NAME")) ;
		assertEquals("bleujin", newNode.get("name")) ;
		assertEquals("bleujin", newNode.get("namE")) ;

	}
	
	
	public void testCaseInSentiveQuery() throws Exception {
		Node newNode = session.newNode();
		newNode.put("nAme", "bleujin") ;
		
		session.commit() ;
		
		Node node = session.createQuery().eq("NAME", "bleujin").findOne() ;
		assertEquals(true, node != null) ;
	}
	
	
	
	
	
	
}
