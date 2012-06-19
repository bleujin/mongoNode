package net.ion.radon.repository.advance;

import net.ion.radon.repository.Explain;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;

public class TestExplain extends TestBaseRepository{

	public void testViewExplain() throws Exception {
		Node root = session.newNode().put("name", "root") ;
		Node bleujin = root.createChild("bleujin").setAradonId("user", "bleujin").put("name", "bleujin") ;
		
		Explain explain = session.createQuery().eq("name", "bleujin").find().explain() ;
		assertEquals(false, explain.useIndex()) ;
		
		assertEquals(true, session.createQuery().aradonGroupId("user", "bleujin").find().explain().useIndex()) ;
	}


	public void testViewLastExplain() throws Exception {
		Node root = session.newNode("root").put("name", "root") ;
		Node bleujin = root.createChild("bleujin").put("name", "bleujin") ;
		session.commit() ;
		
		Node found = session.createQuery().path("/root/bleujin").findOne() ;
		
		Explain explain = session.getAttribute(Explain.class.getCanonicalName(), Explain.class) ;
		assertEquals(true, explain.useIndex()) ;
		
 		assertEquals("_path_id", explain.useIndexName()) ;
	}

}
