package net.ion.radon.repository;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;

public class TestNodeListCursor extends TestBaseRepository {

	
	public void testSkip() throws Exception {
		NodeCursor nc = createSampleNode() ;
		
		assertEquals(4, nc.count()) ;
		assertEquals(2, nc.skip(2).count()) ;
		assertEquals(1, nc.skip(2).limit(1).count()) ;
	}
	
	public void testPage() throws Exception {
		NodeCursor nc = createSampleNode() ;
		
		assertEquals(2, nc.toList(PageBean.create(2, 1)).size()) ;
	}

	public void testSort() throws Exception {
		NodeCursor nc = createSampleNode() ;
		
		List<Node> nodes = nc.ascending("name").toList(PageBean.create(2, 1)) ;
		assertEquals(2, nodes.size()) ;
		
		Node first = nodes.get(0) ;
		assertEquals("bleujin", first.getString("name")) ;
		assertEquals("hero", nodes.get(1).getString("name")) ;
	}
	
	public void testIterator() throws Exception {
		NodeCursor nc = createSampleNode().descending("name").skip(2).limit(2) ;
		
		while(nc.hasNext()){
			Debug.line(nc.next()) ;
		}
	}
	
	
	private NodeCursor createSampleNode() {
		session.newNode().put("name", "bleujin") ;
		session.newNode().put("name", "hero") ;
		session.newNode().put("name", "jin") ;
		session.newNode().put("name", "novision") ;
		
		session.commit() ;
		
		SessionQuery squery = session.createQuery();
		List<Node> datas = squery.find().toList(PageBean.ALL);
		
		
		return NodeListCursor.create(session, squery.getQuery(), datas) ;
	}
}
