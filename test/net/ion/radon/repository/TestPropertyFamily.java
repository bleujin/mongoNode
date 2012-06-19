package net.ion.radon.repository;


public class TestPropertyFamily extends TestBaseRepository{
	
	
	public void testJSONString() throws Exception {
		PropertyFamily fn = PropertyFamily.create().put("a", "a").put("b", 1) ;
		assertEquals("{ \"a\" : \"a\" , \"b\" : 1}", fn.toJSONString()) ;
	}

	
	public void xtestFn() throws Exception {
	}
	
}
