package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.sf.json.JSONObject;

public class TestInnerNode extends TestBaseRepository {

	public void testInterrface() throws Exception {
		
		Node node = session.newNode() ;
		node.setAradonId("people", "bleujin") ;
		node.put("name", "hi") ;
		node.put("firstname", "myname") ;
		
		session.commit() ;
		
		InNode inner = node.inner("name").put("firstName", "bleu").put("secondName", "jin") ;
		session.commit() ;
		
		Node found = session.createQuery().find().next() ;
		
		assertEquals("bleu", ((InNode)node.get("name")).get("firstname")) ;
		
		assertEquals("bleujin", ((InNode)node.get(NodeConstants.ARADON)).getString("uid") ) ;
	}

	public void testComplicateGet() throws Exception {
		Node node = session.newNode() ;
		node.setAradonId("people", "bleujin") ;
		node.inner("name").put("firstName", "bleu").put("secondName", "jin").inner("address").put("loc", "seoul") ;
		node.inner("address").put("loc", "busan") ;

		session.commit() ;
		
		assertEquals("bleu", node.getString("name.firstname")) ;
		assertEquals("bleujin", node.getString("__aradon.uid")) ;
		
		
		Node found = session.createQuery().eq("name.firstname", "bleu").find().next() ;
		assertEquals("bleu", found.getString("name.firstname")) ;
		
		assertEquals("seoul", found.getString("name.address.loc")) ;
		assertEquals("busan", found.getString("address.loc")) ;
	}
	
	

	
	
	
}
