package net.ion.radon.repository;


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

	
}
