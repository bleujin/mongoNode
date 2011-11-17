package net.ion.radon.repository;



public class TestMyRef extends TestBaseRepository{

	public void testRef() throws Exception {
		Node node = session.newNode() ;
		NodeRef ref = node.selfRef() ;
		
		assertEquals(node.getIdentifier(), NodeObject.load(ref.getDBObject()).getString("_query._id")) ;
	}
	
	public void testMergeNodeRef() throws Exception {
		Node bleujin = session.newNode().setAradonId("emp", "bleujin").put("name", "bleujin") ;
		session.commit() ;
		
		
		Node node = session.mergeNode(MergeQuery.createByAradon("emp", "bleujin")) ;

		NodeRef ref = node.selfRef() ;
		
		assertEquals(bleujin.getIdentifier(), NodeObject.load(ref.getDBObject()).getString("_query._id")) ;
	}
	
}
