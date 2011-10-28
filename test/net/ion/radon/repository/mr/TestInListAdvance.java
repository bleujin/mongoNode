package net.ion.radon.repository.mr;

import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Columns;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.TestBaseRepository;

public class TestInListAdvance extends TestBaseRepository{

	public void testRecentSlice() throws Exception {
		createDummyData();
		
		NodeCursor nc = session.createQuery().find(Columns.append().add("name", "address").slice("friend", -2, 2)) ;
		while(nc.hasNext()){
			Node node = nc.next() ;
			if (node.getString("name").equals("bleujin")) {
				assertEquals("novision", ((InNode)node.inlist("friend").get(0)).getString("name")) ; 
				assertEquals("minato", ((InNode)node.inlist("friend").get(1)).getString("name")) ;
			} else if (node.getString("name").equals("hero")){
				assertEquals("pm1200", ((InNode)node.inlist("friend").get(0)).getString("name")) ; 
				assertEquals("bleujin", ((InNode)node.inlist("friend").get(1)).getString("name")) ;
			}
		}
	}
	
	public void testInListFilterSort() throws Exception {
		createDummyData();
		NodeCursor nc = session.createQuery().eq("name", "bleujin").inlist("friend").filter("function isOld(ele, index, array) { return ele.age >= 10;}").withProperty("name", "address").sort("age", true).inlistPage(PageBean.create(2, 2)).find() ;
		
		Node found = nc.next() ;
		assertEquals("bleujin", found.getString("name")) ;
		assertEquals("novision", ((InNode)found.inlist("friend").get(0)).getString("name")) ;
		assertEquals(30, ((InNode)found.inlist("friend").get(0)).get("age")) ;
	}

	public void testInListFilterOuter() throws Exception {
		createDummyData();
		NodeCursor nc = session.createQuery().eq("address", "seoul").ascending("name").inlist("friend").filter("function isOld(ele, index, array) { return ele.age >= 10;}").withProperty("name", "address").sort("age", true).inlistPage(PageBean.create(2, 1)).find() ;
		
		Node found = nc.next() ;
		assertEquals("bleujin", found.getString("name")) ;
		assertEquals("hero", ((InNode)found.inlist("friend").get(0)).getString("name")) ;
		assertEquals(20, ((InNode)found.inlist("friend").get(0)).get("age")) ;
		assertEquals(2, found.inlist("friend").createQuery().find().size()) ;
	}

	
	public void testInListFilterOption() throws Exception {
		createDummyData();
		NodeCursor nc = session.createQuery().eq("address", "seoul").ascending("name").inlist("friend").filter("function isOld(ele, index, array) { return ele.age >= 25;}").find() ;

		Node found = nc.next() ;
		assertEquals(2, found.inlist("friend").createQuery().find().size()) ;
		
		assertEquals(0, session.getModified().size()) ;
	}

	public void testInListFilterOptionSort() throws Exception {
		createDummyData();
		NodeCursor nc = session.createQuery().ascending("name").inlist("friend").filter("function isOld(ele, index, array) { return ele.age >= 25;}").withProperty("name").find().limit(1) ;

		Node found = nc.next() ;
		assertEquals("bleujin", found.getString("name")) ;
		assertEquals(2, found.inlist("friend").createQuery().find().size()) ;
		
		assertEquals(0, session.getModified().size()) ;
	}



	private void createDummyData() throws Exception {
		session.newNode().put("name", "bleujin").put("address", "seoul").
			inlist("friend").
				push(MapUtil.chainMap().put("name", "hero").put("age", 20)).
				push(MapUtil.chainMap().put("name", "novision").put("age", 30)).
				push(MapUtil.chainMap().put("name", "minato").put("age", 25)) ;
		
		session.newNode().put("name", "hero").put("address", "seoul").
			inlist("friend").
				push(MapUtil.chainMap().put("name", "iihi").put("age", 25)).
				push(MapUtil.chainMap().put("name", "pm1200").put("age", 32)).
				push(MapUtil.chainMap().put("name", "bleujin").put("age", 25)) ;
		
		session.commit();
	}

}
