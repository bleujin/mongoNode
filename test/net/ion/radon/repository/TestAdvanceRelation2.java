package net.ion.radon.repository;

import net.ion.radon.core.PageBean;

public class TestAdvanceRelation2 extends TestBaseRepository{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Node dev = session.newNode().setAradonId("dept", "dev").put("deptno", 20).put("dname", "dev") ;
		Node bleujin = session.newNode().setAradonId("employee", "bleujin").put("id", "bleujin").put("name", "bleu").put("age", 20) ;

		session.addReference(dev, "belongto", bleujin) ;
		session.commit() ;
	}
	
	public void testHasProperty() throws Exception {
		Node findNode = session.createQuery().aradonGroupId("employee", "bleujin").findOne() ;

		ReferenceTaragetCursor rc =  session.createRefQuery().to(findNode).find() ;
		assertEquals(true, rc.hasNext()) ;
		
		ReferenceNode refNode = rc.nextReference().getReferenceNode() ;
		assertEquals(true, refNode.hasSourceAradonId())  ;

		assertEquals("dept", refNode.getSourceAradonId().getGroup()) ;
	}
	
	public void testReferenceGroup() throws Exception {
		Node dev = session.createQuery().aradonGroupId("dept", "dev").findOne();
		
		AradonId aid = dev.getAradonId() ;
		assertEquals("dept", aid.getGroup()) ;
		assertEquals("dev", aid.getUid()) ;
	}
	
	public void testViewRef() throws Exception {
		session.changeWorkspace("__reference");
		session.createQuery().find().debugPrint(PageBean.ALL) ;
	}
	
	public void testReference() throws Exception {
		Node findNode = session.createQuery().eq("id", "bleujin").findOne() ;
		
		Node dnode = session.createRefQuery().to(findNode, "belongto").findOne();
		
		
		Node findByRefType = session.createRefQuery().to(findNode, "belongto").findOne();
		assertEquals("dev", findByRefType.getString("dname")) ;

		Node findByAradonKey = session.createRefQuery().to(findNode, ":aradon:dept").findOne() ;
		assertEquals("dev", findByAradonKey.getString("dname")) ;

		Node findByWorkspaceKey = session.createRefQuery().to(findNode, ":workspace:abcd").findOne();
		assertEquals("dev", findByWorkspaceKey.getString("dname")) ;
	}
	
}
