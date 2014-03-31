package net.ion.repository.mongo;

import junit.framework.TestCase;

public class TestFqn extends TestCase{

	public void testRoot() throws Exception {
		Fqn root = Fqn.ROOT ;
		assertEquals("/", root.toString());
		assertEquals("/", root.getParent().toString());
	}
}
