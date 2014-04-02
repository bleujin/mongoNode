package net.ion.repository.mongo.node;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class TestAppend extends TestBaseReset{

	public void testAppendString() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").append("color", "red", "blue").append("color", "white").append("num", 1,2,3,4) ;
				return null;
			}
		}) ;
		
		ReadNode bleujin = session.pathBy("/bleujin");
		bleujin.debugPrint() ;
		
		assertEquals("red", bleujin.property("color").asString());
		assertEquals(1, bleujin.property("num").asInt());
		

		assertEquals(true, bleujin.property("color").asSet().contains("red"));
		assertEquals(true, bleujin.property("color").asSet().contains("blue"));
		assertEquals(true, bleujin.property("color").asSet().contains("white"));

		assertEquals(true, bleujin.property("num").asSet().contains(1));
		assertEquals(true, bleujin.property("num").asSet().contains(2));
		assertEquals(true, bleujin.property("num").asSet().contains(3));
		assertEquals(false, bleujin.property("num").asSet().contains(5));
	}
	
	
	public void testValueOverwrite() throws Exception {
		
	}
	
}
