package net.ion.radon.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;

public class TestNodeSerialize extends TestBaseRepository {

	public void testTransientSession() throws Exception {
		Node node = session.newNode().put("name", "bleujin").append("name", "hero").put("city", "seoul").put("index", 10).setAradonId("test", "bleujin");

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bout);
		output.writeObject(node);

		Node other = (Node) new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray())).readObject();
		assertEquals("bleujin", other.get("name.0"));
		assertEquals(10, other.get("index"));

		try {
			other.put("name", "other");
			fail();
		} catch (IllegalStateException expect) {
		}
	}
	
	public void testJson() throws Exception {
		Node node = session.newNode().put("name", "bleujin").append("name", "hero").put("city", "seoul").put("index", 10).setAradonId("test", "bleujin");
		session.commit() ;
		
		List list = session.createQuery().find().toPropertiesList(PageBean.ALL) ;
		
		Debug.line(JsonParser.fromObject(list)) ;
	}

}
