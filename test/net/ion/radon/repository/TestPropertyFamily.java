package net.ion.radon.repository;

import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestPropertyFamily extends TestBaseRepository{
	
	
	public void testJSONString() throws Exception {
		PropertyFamily fn = PropertyFamily.create().put("a", "a").put("b", 1) ;
		assertEquals("{ \"a\" : \"a\" , \"b\" : 1}", fn.toJSONString()) ;
	}

	
	public void xtestFn() throws Exception {
	}
	
}
