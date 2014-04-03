package net.ion.repository.mongo.node;

import java.util.Calendar;
import java.util.Date;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.util.WriteJobs;

public class TestCreate extends TestBaseReset {

	public void testCreateNode() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20);
				assertEquals(0, wnode.getLastModified());
				return null;
			}
		});

		ReadNode found = session.pathBy("/bleujin");
		assertEquals(true, found.getLastModified() > 0);
	}

	public void testSupportType() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin").property("string", "string").property("sint", "99").property("int", 1).property("long", 1L).property("boolean", true).property("date", new Date());
				return null;
			}
		});

		ReadNode found = session.pathBy("/bleujin");
		assertEquals("string", found.property("string").asString());
		assertEquals("99", found.property("sint").asString());
		assertEquals(99, found.property("sint").asInt());
		assertEquals(1, found.property("int").asInt());
		assertEquals(1L, found.property("long").asLong());
		assertEquals(true, found.property("boolean").asBoolean());
		assertEquals(new Date().getDay(), found.property("date").asDate().getDay());
	}

	public void testCaseInSentiveWhenRead() {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20);
				return null;
			}
		});

		ReadNode found = session.pathBy("/bleujin");
		assertEquals("bleujin", found.property("Name").asString());
		assertEquals("bleujin", found.property("NAME").asString());
		assertEquals("bleujin", found.property("name").asString());
		assertEquals("bleujin", found.property("namE").asString());

		assertEquals("bleujin", found.property("Name").asObject());
		assertEquals("bleujin", found.property("NAME").asObject());
		assertEquals("bleujin", found.property("name").asObject());
		assertEquals("bleujin", found.property("namE").asObject());

	}

	public void testCaseInSentiveWhenWrite() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode wnode = wsession.pathBy("/bleujin").property("Name", "bleujin").property("age", 20);
				return null;
			}
		});

		assertEquals("bleujin", session.pathBy("/bleujin").property("NAME").asString());
	}

	public void testCreateChildNode() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				WriteNode bleu = wsession.pathBy("/bleu").property("Name", "bleu").property("age", 20);
				bleu.child("jin").property("Name", "jin").property("age", 20);
				return null;
			}
		});

		assertEquals(1, session.root().children().toList().size());
		assertEquals(20, session.pathBy("/bleu").property("age").asLong());
		assertEquals("jin", session.pathBy("/bleu/jin").property("name").asString());
	}

	public void testOverwriteWhenDupPath() throws Exception {

		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin");
				return null;
			}
		});

		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "hero");
				return null;
			}
		});

		assertEquals("hero", session.pathBy("/bleujin").property("name").asString());
	}

	public void testReservedProperty() throws Exception {
		session.tranSync(WriteJobs.HELLO);

		ReadNode found = session.pathBy("/bleujin");

		assertEquals("/bleujin", found.fqn().toString());
		assertEquals("/bleujin", found.id().toString());
		assertEquals("/bleujin", found.property("_id").asString());
		assertEquals("/", found.property("_parent").asString());
	}

}
