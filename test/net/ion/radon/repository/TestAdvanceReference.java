package net.ion.radon.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import net.ion.framework.db.Rows;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.myapi.AradonQuery;

public class TestAdvanceReference extends TestBaseRepository{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Node dev = session.newNode() ;
		dev.setAradonId("dept", "dev") ;
		dev.put("deptno", 20) ;
		dev.put("dname", "dev") ;
		
		Node bleujin = session.newNode() ;
		bleujin.setAradonId("employee", "bleujin") ;
		bleujin.put("id", "bleujin") ;
		bleujin.put("name", "bleu") ;
		bleujin.put("age", 20) ;

		session.addReference(dev, "belongto", bleujin) ;
		session.commit() ;
		
		
	}
	
	public void testHasProperty() throws Exception {

		Node findNode = session.createQuery().aradonGroupId("id", "bleujin").findOne() ;

		ReferenceTaragetCursor rc =  session.createRefQuery().to(findNode).find() ;
		assertEquals(true, rc.hasNext()) ;
		
		ReferenceNode refNode = rc.nextReference().getReferenceNode() ;
		assertEquals(true, refNode.hasSourceAradonId())  ;

		assertEquals("dept", refNode.getSourceAradonId().getGroup()) ;
	}
	
	
	public void testReferenceGroup() throws Exception {
		Node dev = session.createQuery().aradonGroupId("deptno", 20).findOne();
		
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
		
		// findNode.fromNode("/w/dept") ;;
		
		// Node deptNode = findNode.refSrcNode(ReferenceSourceQuery.create("dept")) ;
		
		Node dnode = session.createRefQuery().to(findNode, "belongto").findOne();
		
		
		Node findByRefType = session.createRefQuery().to(findNode, "belongto").findOne();
		assertEquals("dev", findByRefType.getString("dname")) ;

		Node findByAradonKey = session.createRefQuery().to(findNode, ":aradon:dept").findOne() ;
		assertEquals("dev", findByAradonKey.getString("dname")) ;

		Node findByWorkspaceKey = session.createRefQuery().to(findNode, ":workspace:abcd").findOne();
		assertEquals("dev", findByWorkspaceKey.getString("dname")) ;
	
		
		// TODO : not imple
//		Node dnode3 = session.getReferenceManager().find(ReferenceQuery.to(findNode, "/p/")).next() ;
//		Debug.debug(dnode, dnode.get("__path")) ;
	}
	
}
