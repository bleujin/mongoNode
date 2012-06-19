package net.ion.radon.repository;

import java.util.Date;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;

public class TestSample extends TestBaseRepository{

	
	public void testCreateNode() throws Exception {
		Node node = session.newNode() ;
		node.put("name", "bleujin").put("age", 20) ;
		
		Node hero = node.createChild("hero") ;
		hero.put("name", "hero").put("birth", new Date()) ;
		
		Node n = session.newNode() ;
		n.inner("address").put("city", "seoul").put("street", 20) ;
		n.inner("name").put("firstname", "bleu").put("lastname", "jin") ;
		
		session.commit() ;

	}
	
	public void testFind() throws Exception {
		testCreateNode() ;
		
		Node node = session.createQuery().eq("name", "bleujin").ascending("age").findOne() ;
		Debug.debug(node) ;
		
		node.getChild().debugPrint(PageBean.ALL) ;
		session.createQuery().eq("address.city", "seoul").eq("name.lastname", "jin").find().debugPrint(PageBean.ALL) ;
		
		
	}
}
