package net.ion.radon.repository;

import net.ion.framework.util.Debug;


public class TestMergeNode extends TestBaseRepository{
	
	
	public void testMergeNode() throws Exception {
		session.mergeNode(MergeQuery.createByAradon("emp", "bleujin")).put("name", "bleujin") ;
		int count = session.commit() ; // not saved ;
		assertEquals(1, count) ;
		
		assertEquals("bleujin", session.createQuery().findOne().getString("name")) ;
	}


	public void testMetaGet() throws Exception {
		Node newNode = session.newNode("bleujin").setAradonId("emp", "bleujin").put("name", "hero").put("age", 20) ;
		session.commit() ;
		
		Node metaNode = session.mergeNode(MergeQuery.createByAradon("emp", "bleujin")) ;
		for (String key : Columns.MetaColumns) {
			assertEquals(newNode.get(key), metaNode.get(key)) ;
		}
	}
	
	public void testMergeOnExist() throws Exception {
		Node node = session.newNode("bleujin").setAradonId("emp", "bleujin").put("name", "hero").put("age", 20) ;
		session.commit() ;

		Node mnode = session.mergeNode(MergeQuery.createByAradon("emp", "bleujin")).put("name", "hero").put("nage", 20) ;
		assertEquals(false, mnode.isNew()) ;

		
		int count = session.commit() ; // not saved ;
		assertEquals(1, count) ;
		
		assertEquals(node.getIdentifier(), mnode.getIdentifier()) ;
		
		Node found = session.createQuery().findOne();
		assertEquals("hero", found.getString("name")) ;
		assertEquals(true, found.get("age") == null) ;
		assertEquals(20, found.get("nage")) ;
		assertEquals("emp", found.getAradonId().getGroup()) ;
	}

	public void testNotExist() throws Exception {
		Node mnode = session.mergeNode(MergeQuery.createByAradon("emp", "bleujin")).put("name", "hero").put("nage", 20) ;
		assertEquals(true, mnode.isNew()) ;
		
		int count = session.commit() ; // not saved ;
		assertEquals(1, count) ;
		
		Node found = session.createQuery().findOne();
		assertEquals("hero", found.getString("name")) ;
		assertEquals(20, found.get("nage")) ;
		assertEquals("emp", found.getAradonId().getGroup()) ;
	}
	
	public void testLastModified() throws Exception {
		Node node = session.newNode("bleujin").setAradonId("emp", "bleujin").put("name", "hero").put("age", 20) ;
		session.commit() ;
		long createTime = session.createQuery().findOne().getLastModified() ;

		Thread.sleep(100) ;
		
		Node mnode = session.mergeNode(MergeQuery.createByAradon("emp", "bleujin")).put("name", "hero").put("nage", 20) ;
		int count = session.commit() ; // not saved ;
		assertEquals(1, count) ;
		
		assertEquals(node.getIdentifier(), mnode.getIdentifier()) ;
		
		Node found = session.createQuery().findOne();
		assertEquals("hero", found.getString("name")) ;
		assertEquals(true, found.get("age") == null) ;
		assertEquals(20, found.get("nage")) ;
		
		
		assertEquals(true, found.getLastModified() > createTime) ;
	}

}
