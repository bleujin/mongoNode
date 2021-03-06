package net.ion.repository.mongo.node;

import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestPathBy extends TestBaseReset {

	public void testFind() throws Exception {
		createHelloNode();
		assertEquals("bleujin", session.pathBy("/bleujin").property("name").asString());
	}


	public void testFindByPath() throws Exception {

		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleu").property("name", "bleu").child("jin").property("name", "jin").child("hero").property("name", "hero");
				return null;
			}
		});

		assertEquals("bleu", session.pathBy("/bleu").property("name").asString());
		assertEquals("jin", session.pathBy("/bleu/jin").property("name").asString());
		assertEquals("hero", session.pathBy("/bleu/jin/hero").property("name").asString());
	}


}
