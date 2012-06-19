package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;

public class TestBasicInner extends TestBaseRepository{

	
	public void testUse() throws Exception {
		
		Node node = session.newNode() ;
		node.setAradonId("root.p1.p2", "abcd.1").put("artid", 1).put("catid", "abcd").inner("address").put("xloc", 20).put("yloc", 30) ;
		node.inner("color").put("name", "red").put("rgb", 2000000) ;
		session.commit() ;
		
		session.createQuery().aradonGroupId("root", "abcd.1").update(MapUtil.chainMap().put("artid", 3).put("address.yloc", 50).put("new", "hi")) ;

		Node found = session.createQuery().eq("address.xloc", 20).lt("color.rgb", 2345670).findOne() ;
		assertEquals(true, found != null) ;
		assertEquals(50, found.get("address.yloc")) ;
		assertEquals(3, found.get("artid")) ;
		
		assertEquals(1, session.createQuery().aradonGroup("root.p1").find().count()) ;  
	}
	
	
	
	
	public void xtestInListQuery() throws Exception {
		Node node = session.newNode() ;
		node.setAradonId("article", "abcd.1").put("artid", 1).put("catid", "abcd").inlist("afield").push(MapUtil.chainKeyMap().put("afieldId", "address").put("xloc", 30).put("yloc", 30)) ;
		
		session.commit() ;
		session.createQuery().eleMatch("afield", PropertyQuery.create().eq("afieldId", "address").eq("xloc", 30)).find().debugPrint(PageBean.ALL) ;
	}
}
