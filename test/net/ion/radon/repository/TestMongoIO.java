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

public class TestMongoIO extends TestBaseRepository {


	public Node create(int i) throws Exception {

		Node node = session.newNode();
		node.put("name", "bleujin");
		node.append("name", "hero");
		node.put("city", "seoul");
		node.put("index", i);
		node.setAradonId("test", "bleujin" + i);
		session.commit();
		return node ;
	}

	public void testSelectAll() throws Exception {
		NodeCursor nc = session.createQuery().find();
		while (nc.hasNext()) {
			Debug.debug(nc.next());
		}
	}
	
	
	public void testUpdate() throws Exception {
		create(0) ;
		Node bleujin = session.createQuery().eq("name", "bleujin").findOne();
		Debug.debug(bleujin) ;
		
		bleujin.put("age", 20) ;
		
		Node childNode = bleujin.createChild("abcd") ;

		session.commit() ;
	}

	public void testFind() throws Exception {
		Node newNode = create(0) ;
		Node node = session.createQuery().id(newNode.getIdentifier()).findOne();
		assertNotNull(node) ;
	}

	public void testSelect() throws Exception {
		for (int i = 0; i < 10; i++) {
			create(i) ;
		}
		List<Node> nc =  createQuery().eq("name", "bleujin").descending("index").find().toList(PageBean.create(3, 2)) ;
		
		for (Node n : nc ) {
			Debug.debug(n) ;
		}
//		nc.debugPrint(PageBean.TEN) ;
	}

	public void testDelete() throws Exception {
		Node newNode = create(0) ;
		Node node = session.createQuery().id(newNode.getIdentifier()).findOne();
		session.remove(node) ;
	}

	public void testRelation() throws Exception {
		session.dropWorkspace();

		Node bleu = session.newNode();
		bleu.put("name", "bleu");
		bleu.setAradonId("test", "bleu");
		
		Node jin = session.newNode() ;
		jin.put("name", "jin");
		jin.put("greeting", 44564);
		jin.setAradonId("test", "jin");

		Node heeya = session.newNode() ;
		heeya.put("name", "heeya");
		heeya.put("greeting", new Date());
		heeya.setAradonId("test", "heeya");
		
		
		session.addReference(bleu, "friend", jin) ;
		session.addReference(bleu, "pair", heeya) ;
		
		session.commit();
		ReferenceTaragetCursor refCursor =  session.createRefQuery().from(bleu).find() ;
		while(refCursor.hasNext()) {
			Debug.debug(refCursor.next()) ;
		}
	}

	
	public void testOtherRelation() throws Exception {
		session.dropWorkspace() ;

		Node bleu = session.newNode("bleu");
		bleu.put("name", "bleu");
		bleu.setAradonId("test", "bleu");
		
		session.changeWorkspace("w2") ;
		session.dropWorkspace() ;
		
		Node jin = session.newNode() ;
		jin.put("name", "jin");
		jin.append("name", "hero");
		jin.put("greeting", 44564);
		jin.setAradonId("test", "jin");
		
		session.commit();

		session.addReference(bleu, "friend", jin) ;
		
		ReferenceTaragetCursor refCursor = session.createRefQuery().from(bleu).find() ;
		while(refCursor.hasNext()) {
			Debug.debug(refCursor.next()) ;
		}
	}

}
