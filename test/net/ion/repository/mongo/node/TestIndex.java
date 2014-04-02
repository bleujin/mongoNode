package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestIndex extends TestBaseReset{

	
	public void testCreateIndex() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				for (int i = 0; i < 10 ; i++) {
					wsession.pathBy("/index/" + i).property("index", i).property("nindex", i) ;
				}
				wsession.ensureIndex("index_idx").ascending("index").unique(true).background(false).create() ;
				return null;
			}
		}) ;
		
		Explain explain = session.pathBy("/index").children().between("index", 3, 5).eachNode(new ReadChildrenEach<Explain>() {
			@Override
			public Explain handle(ReadChildrenIterator citer) {
				return citer.explain();
			}
		}) ;
		
		assertEquals(true, explain.useIndex());
		assertEquals("index_idx", explain.useIndexName()) ;
	}
	
	
	public void testHint() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				for (int i = 0; i < 10 ; i++) {
					wsession.pathBy("/index/" + i).property("index", i).property("nindex", i) ;
				}
				wsession.ensureIndex("index_idx1").ascending("index").unique(true).background(false).create() ;
				wsession.ensureIndex("index_idx2").ascending("nindex").unique(true).background(false).create() ;
				return null;
			}
		}) ;
		
		Explain explain = session.pathBy("/index").children().between("index", 3, 5).between("nindex", "3", "5").hint("index_idx2").eachNode(new ReadChildrenEach<Explain>() {
			@Override
			public Explain handle(ReadChildrenIterator citer) {
				return citer.explain();
			}
		}) ;
		assertEquals("index_idx2", explain.useIndexName());
	}
	
	
	public void testDropIndex() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				for (int i = 0; i < 10 ; i++) {
					wsession.pathBy("/index/" + i).property("index", i).property("nindex", i) ;
				}
				wsession.ensureIndex("index_idx").ascending("index").unique(true).background(false).create() ;
				return null;
			}
		}) ;

		
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.ensureIndex("index_idx").ascending("index").unique(true).background(false).drop() ;
				return null;
			}
		}) ;
		
		Explain explain = session.pathBy("/index").children().between("index", 3, 5).eachNode(new ReadChildrenEach<Explain>() {
			@Override
			public Explain handle(ReadChildrenIterator citer) {
				return citer.explain();
			}
		}) ;
		
		assertEquals(false, explain.useIndex());
	}
}
