package net.ion.radon.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

}
