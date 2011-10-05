package net.ion.radon.repository.innode;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;

public class TestInListMDL extends  TestBaseInListQuery{
	
	public void testAppend() throws Exception {
		createNode();
		Node found = session.createQuery().findOne() ;
		assertEquals(5, found.inlist("people").createQuery().find().size()) ;
		
		List<InNode> subList = found.inlist("people").createQuery().lte("index", 1).find() ;
		
		assertEquals(0, subList.get(0).get("index")) ;
		assertEquals(1, subList.get(1).get("index")) ;
	}

	
	public void testRemove() throws Exception {
		
		createNode();
		Node found = session.createQuery().findOne() ;
		int result = found.inlist("people").createQuery().eq("index", 1).remove() ;
		assertEquals(1, result) ;
		
		assertNull(found.inlist("people").createQuery().eq("index", 1).findOne()) ; // not saved
		assertNotNull(session.createQuery().findOne().inlist("people").createQuery().eq("index", 1).findOne()) ; 
		session.commit() ;
		
		Node newFound = session.createQuery().findOne() ;
		assertNull(newFound.inlist("people").createQuery().eq("index", 1).findOne()) ;
	}
	

	public void testRemove2() throws Exception {
		
		createNode();
		Node found = session.createQuery().findOne() ;
		found.inlist("people").createQuery().gte("index", 3).remove() ;
		
		assertEquals(3, found.inlist("people").createQuery().find().size()) ;
		session.commit() ;
		
		Node newFound = session.createQuery().findOne() ;
		assertEquals(3, newFound.inlist("people").createQuery().find().size()) ;
	}
	

	public void testInListNodeUpdate() throws Exception {
		createNode();
		Node found = session.createQuery().findOne() ;
		found.inlist("people").createQuery().gte("index", 3).update(MapUtil.chainMap().put("coffie", "top").put("index", 4)) ;
		
		assertEquals(2, found.inlist("people").createQuery().exist("coffie").find().size()) ;
		session.commit() ;
		
		Node newFound = session.createQuery().findOne() ;
		assertEquals(2, newFound.inlist("people").createQuery().exist("coffie").find().size()) ;
		assertEquals(2, newFound.inlist("people").createQuery().eq("index", 4).find().size()) ;
	}
	
	

	public void testInListNodeUpdate2() throws Exception {
		Node bleujin = session.newNode().put("name", "bleujin") ;
		bleujin.inlist("greeting").push(MapUtil.chainMap().put("eng", "hello")) ;
		bleujin.inlist("greeting").push(MapUtil.chainMap().put("kor", "hi")) ;
		session.commit() ;
		
		Node found = session.createQuery().findOne() ;
		found.inlist("greeting").createQuery().eq("eng", "hello").update(MapUtil.chainMap().put("eng", "하이").put("index", 4)) ;
		session.commit() ;
		
		Node newFound = session.createQuery().findOne() ;
		
		Debug.line(newFound) ;
		
		assertEquals(1, newFound.inlist("greeting").createQuery().exist("index").find().size()) ;
		assertEquals(1, newFound.inlist("greeting").createQuery().eq("eng", "하이").find().size()) ;
	}
	
	
	
	
	

	public void testPush() throws Exception {
		session.newNode() ;
		session.commit() ;
		
		session.createQuery().inlist("message").push(MapUtil.chainMap().put("greeting", "red"));
		session.createQuery().inlist("message").push(MapUtil.chainMap().put("greeting", "red"));

		assertEquals(2, session.createQuery().findOne().inlist("message").createQuery().find().size()) ;
	}

	public void xtestUpdate() throws Exception {
		Node bleujin = session.newNode().put("name", "bleujin") ;
		bleujin.inlist("greeting").push(MapUtil.chainMap().put("eng", "hello")) ;
		bleujin.inlist("greeting").push(MapUtil.chainMap().put("kor", "hi")) ;

		Node hero = session.newNode().put("name", "hero") ;
		hero.inlist("greeting").push(MapUtil.<String, Object>chainMap().put("eng", "hello").toMap()) ;
		hero.inlist("greeting").push(MapUtil.<String, Object>chainMap().put("kor", "hi").toMap()) ;

		session.commit() ;

	}
	

	public void testPushOnNewState() throws Exception {
		Node node = session.newNode().put("greeting", "hi") ;
		node.inlist("hobby").push(MapUtil.chainMap().put("name", "pramodel")) ;
		node.inlist("hobby").push(MapUtil.chainMap().put("name", "baseball")) ;
		
		session.commit() ;
		
		
		Node found = session.createQuery().findOne();
		try {
			found.inlist("hobby").push(MapUtil.chainMap().put("name", "movie")) ;
			fail() ;
		} catch(IllegalStateException expect) {
		} catch(Exception ex){
			fail() ;
		}
		
		
		
		
	}
	
	
	
	
}
