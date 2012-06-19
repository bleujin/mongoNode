package net.ion.radon.repository.minato;

import net.ion.radon.repository.Node;

public class TestToRelation extends TestRepo {

	public void testTargetRemove() throws Exception {
		dataClear();
		
		Node minato = session.newNode().put("name", "minato");
		Node gnic = session.newNode().put("name", "gnic");
		Node bleujin = session.newNode().put("name", "bleujin");
		
		gnic.toRelation("friend", minato.selfRef());
		bleujin.toRelation("friend", minato.selfRef());
		session.commit();
		
		assertEquals(2, minato.relation("friend").froms().count());
		int count = session.commit();
		assertEquals(0, count);
		int result = gnic.relation("friend").remove(minato.selfRef());
//		int result = gnic.relation("friend").remove();
		
		count = session.commit();

		assertEquals(1, result);
		assertEquals(1, count);
		assertEquals(1, minato.relation("friend").froms().count());
	}
	
}
