package net.ion.radon.repository;

import java.util.List;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;

import org.bson.types.ObjectId;

public class TestWorkspace extends TestBaseRepository{
	
	public void testConnect() throws Exception {

		Node node = session.newNode("bleujin") ;
		node.setAradonId("test", "bleujin") ;
		node.put("name", "bleujin") ;
		node.put("address", "seoul") ;
		
		NodeCursor nc = session.createQuery().find() ;
		NodeScreen ns = nc.screen(PageBean.create(10, 1)) ;
		
		session.commit();
		Debug.debug(ns.getPageMap()) ;
	}
	
	public void testFind() throws Exception {
		Node find = session.createQuery().aradonGroupId(NodeConstants.ID, new ObjectId("4d899294ad9f4bb963749534")).findOne() ;
		
		Debug.debug(find) ;
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
		Debug.debug(nodes.get(0)) ;
		
	}
	
	
	public void testSessionSave() throws Exception {
		
		Node newNode = session.newNode("child0") ;
		assertEquals("child0", newNode.getName()) ;
		
		session.newNode("child1") ;
		
		session.commit() ;
		assertEquals(2, session.createQuery().find().count()) ;
		
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
	
	public void testPath() throws Exception {
		Node newNode = session.newNode("name") ;
		assertEquals("/name", newNode.getPath()) ;
		
		Node child = newNode.createChild("child") ;
		assertEquals("/name/child", child.getPath()) ;
		
		
		try {
			newNode.createChild("/child") ;
			fail() ;
		} catch(IllegalArgumentException ignore) {
		}
	}

	
	public void testFindByPath() throws Exception {
		
		Node newNode = session.newNode("name") ;
		
		
		Node child = newNode.createChild("child") ;
		child.append("name", "bleujin") ;
		session.commit();
		
		Node load = session.createQuery().findByPath("/name/child") ;
		assertEquals("bleujin", load.getString("name")) ;
		
		
		Node gchild = load.createChild("gchild") ;
		gchild.append("name", "hero") ;
		session.commit();
		
		Node gload = session.createQuery().findByPath("/name/child/gchild") ;
		assertEquals("hero", gload.getString("name")) ;
		
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
