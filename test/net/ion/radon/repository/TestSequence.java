package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;


public class TestSequence extends TestBaseRepository{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		session.changeWorkspace("_sequence").dropWorkspace() ;
	}
	
	public void testInit() throws Exception {
		
		ISequence seq = session.getSequence("test", "bleujin");
		seq.reset() ;

		long cval = seq.currVal() ;
		assertEquals(0, cval) ;
	}
	
	public void testNext() throws Exception {
		ISequence seq = session.getSequence("test", "bleujin");
		seq.reset() ;
		seq.currVal() ;
		long cval = seq.nextVal();
		
		Node seqNode = session.getWorkspace("_sequence").findOne(session, PropertyQuery.createByPath("/test_bleujin"), Columns.ALL) ;
		assertEquals(true, seqNode != null) ;
		assertEquals(1, seqNode.getAsInt("seq")) ;
		assertEquals(1, cval) ;
	}
	

	public void testUse() throws Exception {
		session.changeWorkspace("my") ;
		session.dropWorkspace() ;
		
		Node node = session.newNode().put("name", "bleujin").put("seq", session.getSequence("my", "num").nextVal()) ;
		session.commit() ;
		
		
		Debug.debug(session.getCurrentWorkspaceName(), session.createQuery().find().toList(PageBean.ALL)) ;
		assertEquals(1, session.createQuery().find().count() );

		assertEquals(true, session.changeWorkspace("_sequence").createQuery().path("/my_num").existNode()) ;
	}
	
	
	public void testNextVal() throws Exception {
		ISequence seq = session.getSequence("my", "test") ;
		seq.reset() ;
		
		for (int i = 1; i <= 5 ; i++) {
			assertEquals(i , seq.nextVal()) ;
		}
	}

	
	public void xtestGetCache() throws Exception {
		ISequence seq = session.getSequence("my", "test") ;
		Debug.line(seq.currVal()) ;
	}
	
}
