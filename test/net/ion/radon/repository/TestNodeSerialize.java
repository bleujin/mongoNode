package net.ion.radon.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.Workspace;

import com.mongodb.Mongo;

import junit.framework.TestCase;

public class TestNodeSerialize extends TestBaseRepository{

	private void create(int i) throws Exception {

		Node node = session.newNode("bleujin");
		node.put("name", "bleujin");
		node.append("name", "hero");
		node.put("city", "seoul");
		node.put("index", i);
		node.setAradonId("test", "bleujin");
		session.commit();
	}
	
	public void testSerialize() throws Exception {

		Node node = session.newNode();
		node.put("name", "bleujin");
		node.append("name", "hero");
		node.put("city", "seoul");
		node.put("index", 10);
		node.setAradonId("test", "bleujin");
		
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bout) ;
		output.writeObject(node) ;
		
		
		Node other = (Node) new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray())).readObject() ;
		Debug.debug(other.get("name"), other.get("city"), other.get("index")) ;
		other.put("name", "other") ;
		
		
		Debug.debug(node.get("name"), other.get("name")) ;
		
		
	}
	
	
}
