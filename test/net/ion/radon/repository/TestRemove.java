package net.ion.radon.repository;


public class TestRemove extends TestBaseRepository {

	private Node createSample(int i) throws Exception {

		Node node = session.newNode();
		node.put("name", "bleujin");
		node.append("name", "hero");
		node.put("city", "seoul");
		node.put("index", i);
		node.setAradonId("test", "bleujin" + i);
		session.commit();
		return node ;
	}

	public void testNodeRemove() throws Exception {
		Node newNode = createSample(0) ;
		Node node = session.createQuery().id(newNode.getIdentifier()).findOne();
		session.remove(node) ;
	}

	public void testQueryRemove() throws Exception {
		createSample(1) ;
		createSample(2) ;
		
		Node node = session.newNode();
		node.put("name", "heeya");
		session.commit();

		int result = session.createQuery().eq("name", "bleujin").remove();
		assertEquals(2, result);
		assertEquals(1, session.createQuery().find().count());
	}


	
}
