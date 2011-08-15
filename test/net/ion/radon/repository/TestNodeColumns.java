package net.ion.radon.repository;

public class TestNodeColumns extends TestBaseRepository {
	
	
	public void testColumn() throws Exception {
		NodeColumns columns =  NodeColumns.create("id", "name", "group.key");
		
		assertEquals(3, columns.size());
	}
	
	public void testLaabel() throws Exception {
		NodeColumns columns =  NodeColumns.create("id", "name", "group.key", "group.key another");

		assertEquals("id", columns.get(1).getLabel());
		assertEquals("key", columns.get(3).getLabel());
		assertEquals("another", columns.get(4).getLabel());
	}

	
	public void testTargetColumn() throws Exception {
		NodeColumns columns =  NodeColumns.create("id", "name", "group.key", "group.key another");
		
		assertEquals("id", columns.get(1).getLabel());
		assertEquals(true, columns.get(3) instanceof ReferenceColumn);
		assertEquals("key", columns.get(3).getLabel());
		assertEquals(true, columns.get(4) instanceof ReferenceColumn);
		assertEquals("another", columns.get(4).getLabel());
	}
	

	public void testTargetColumnName() throws Exception {
		NodeColumns columns =  NodeColumns.create("id", "name", "group.key", "group.key another");
		
		assertEquals("id", columns.get(1).getLabel());
		assertEquals("name", columns.get(2).getLabel());
		assertEquals("key", columns.get(3).getLabel());
		assertEquals("another", columns.get(4).getLabel());
	}
	
	
	
}
