package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;


public class TestSequence extends TestBaseRepository{

	public void testInit() throws Exception {
		ISequence seq = session.getSequence("test", "bleujin");
		seq.reset() ;

		long cval = seq.currVal() ;
		assertEquals(0, cval) ;
	}
	
	public void testNext() throws Exception {
		ISequence seq = session.getSequence("test", "bleujin");
		seq.reset() ;
		long cval = seq.nextVal();
		assertEquals(1, cval) ;
	}
	

	public void testCreate() throws Exception {
		ISequence seq1 = session.getSequence("test", "bleujin");
		ISequence seq2 = session.getSequence("test", "bleujin");
		assertTrue(seq1 == seq2) ;
	}
	
	public void testCache() throws Exception {
		ISequence seq = session.getSequence("test", "bleujin");
		seq.reset() ;
		
		for (int i = 0; i < 10 ; i++) {
			assertEquals(i + 1, seq.nextVal()) ; 
			assertEquals(10 - i - 1, seq.getCacheRemained()) ;
		}
	}

	public void testUse() throws Exception {
		session.changeWorkspace("my") ;
		session.dropWorkspace() ;
		
		Node node = session.newNode().put("name", "bleujin").put("seq", session.getSequence("my", "num").nextVal()) ;
		session.commit() ;
		
		
		Debug.debug(session.getCurrentWorkspaceName(), session.createQuery().find().toList(PageBean.ALL)) ;
		assertEquals(1, session.createQuery().find().count() );

		assertEquals(true, session.changeWorkspace("_sequence").createQuery().findByPath("/my_num") != null) ;
	}
	
	
	public void xtestCacheLimit() throws Exception {
		session.changeWorkspace("my") ;
		session.dropWorkspace() ;
		
		ISequence seq = session.getSequence("my", "test") ;
		
		for (int i = 0; i < 5 ; i++) {
			Debug.line(seq.nextVal()) ;
		}
	}

	
	public void xtestGetCache() throws Exception {
		ISequence seq = session.getSequence("my", "test") ;
		Debug.line(seq.currVal()) ;
	}
	
}
