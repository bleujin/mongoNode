package net.ion.repository.mongo;

import junit.framework.TestCase;

public class TestFqn extends TestCase{

	public void testRoot() throws Exception {
		Fqn root = Fqn.ROOT ;
		assertEquals("/", root.toString());
		assertEquals("/", root.getParent().toString());
	}
	
	public void testDepth() throws Exception {
		assertEquals(0, Fqn.ROOT.depth()) ; 
		assertEquals(0, Fqn.fromString("/").depth()) ; 
		assertEquals(1, Fqn.fromString("/emps").depth()) ; 
		assertEquals(2, Fqn.fromString("/emps/bleujin").depth()) ; 
	}
}
