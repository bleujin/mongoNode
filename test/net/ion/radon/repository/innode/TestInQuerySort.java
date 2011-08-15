package net.ion.radon.repository.innode;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;
import net.sf.json.JSONObject;

public class TestInQuerySort extends  TestBaseInQuery {
	
	public void testPath() throws Exception {
		Node node = createNode();
		
		Debug.debug(node.inner("people").createQuery().findOne()) ;
		node.inner("people").createQuery().descending("index").findOne() ;
	}
	

	
	public void testSort() throws Exception {
		createNode();
		Node found = session.createQuery().findOne() ;
		
		assertEquals(0, found.inner("people").createQuery().ascending("address.city").ascending("index").findOne().get("index")) ;
		assertEquals(0, found.inner("people").createQuery().ascending("not.key").findOne().get("index")) ;
	}

	
	public void testSortPage() throws Exception {
		createNode();
		Node found = session.createQuery().findOne() ;
		
		List<InNode> founds = found.inner("people").createQuery().ascending("address.city").ascending("index").find(PageBean.create(2,2));
		assertEquals(2, founds.size()) ;
		assertEquals(2, founds.get(0).getAsInt("index")) ;
		assertEquals(3, founds.get(1).getAsInt("index")) ;
	}
	

	
	

	
}