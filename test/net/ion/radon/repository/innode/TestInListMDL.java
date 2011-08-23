package net.ion.radon.repository.innode;

import java.util.Collections;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;
import net.sf.json.JSONObject;

public class TestInListMDL extends  TestBaseInListQuery{
	

	public void testAppend() throws Exception {
		createNode();
		Node found = session.createQuery().findOne() ;
		
		List<InNode> subList = found.inner("people").createQuery().find(PageBean.create(2, 1)) ;
		
		assertEquals(0, subList.get(0).get("index")) ;
		assertEquals(1, subList.get(1).get("index")) ;
	}

	
	public void testRemove() throws Exception {
		
		createNode();
		Node found = session.createQuery().findOne() ;
		found.inner("people").createQuery().eq("index", 1).remove() ;
		
		assertNull(found.inner("people").createQuery().eq("index", 1).findOne()) ;
		session.commit() ;
		
		Node newFound = session.createQuery().findOne() ;
		assertNull(newFound.inner("people").createQuery().eq("index", 1).findOne()) ;
	}
	

	public void testRemove2() throws Exception {
		
		createNode();
		Node found = session.createQuery().findOne() ;
		found.inner("people").createQuery().gte("index", 3).remove() ;
		
		assertEquals(3, found.inner("people").createQuery().find().size()) ;
		session.commit() ;
		
		Node newFound = session.createQuery().findOne() ;
		assertEquals(3, newFound.inner("people").createQuery().find().size()) ;
	}
	

	public void testUpdate() throws Exception {
		
		createNode();
		Node found = session.createQuery().findOne() ;
		found.inner("people").createQuery().gte("index", 3).put("coffie", "top").put("index", 4).update() ;
		
		assertEquals(2, found.inner("people").createQuery().exist("coffie").find().size()) ;
		session.commit() ;
		
		Node newFound = session.createQuery().findOne() ;
		assertEquals(2, newFound.inner("people").createQuery().exist("coffie").find().size()) ;
		assertEquals(2, newFound.inner("people").createQuery().eq("index", 4).find().size()) ;
	}
	
	


}
