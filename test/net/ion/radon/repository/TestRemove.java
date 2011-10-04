package net.ion.radon.repository;

import java.util.Date;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.TestAradon;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.ReferenceTaragetCursor;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.Workspace;

import com.mongodb.Mongo;

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
