package net.ion.radon.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;

import org.apache.commons.collections.Closure;

public class TestNodeCursor extends TestBaseRepository{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createSample(10) ;
	}
	
	public void testSelectAll() throws Exception {
		NodeCursor nc = session.createQuery().find();
		
		assertEquals(10, nc.count()) ;
	}

	public void testPaging() throws Exception {
		List<Node> nc =  createQuery().eq("name", "bleujin").descending("index").find().toList(PageBean.create(3, 2)) ;
		
		assertEquals(3, nc.size()) ;
	}

	public void testSort() throws Exception {
		PageBean page = PageBean.create(3, 2);
		List<Node> nc =  createQuery().eq("name", "bleujin").descending("index").find().toList(page) ;
		
		int predefined = page.getEndLoc() ;
		for (Node node : nc) {
			assertEquals(predefined--, node.get("index")) ;
		}
	}

	
	public void testEach() throws Exception {
		
		final AtomicInteger sum = new AtomicInteger() ;
		createQuery().eq("name", "bleujin").find().each(PageBean.ALL, new Closure() {
			public void execute(Object node) {
				sum.addAndGet(((Node)node).getAsInt("index")) ;
			}
		}) ;
		
		assertEquals(45, sum.intValue()) ;
	}
	
	public void testDebugPrint() throws Exception {
		createQuery().eq("name", "notfound").find().debugPrint(PageBean.ALL) ;
		
	}
	
	public void testScreen() throws Exception {

		NodeCursor nc = session.createQuery().find() ;
		NodeScreen ns = nc.screen(PageBean.create(4, 1)) ;
		
		List<Map<String, ?>> mapList = ns.getPageMap() ;
		assertEquals(4, mapList.size()) ;
		assertEquals("bleujin", mapList.get(0).get("name")) ;
		assertEquals(10, ns.getScreenSize()) ;
	}
	
	
	
	private void createSample(int max) {
		for (int i = 0; i < max; i++) {
			session.newNode().put("name", "bleujin").put("index", i) ;
		}
		session.commit() ;
	}

}
