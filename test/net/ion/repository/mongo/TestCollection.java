package net.ion.repository.mongo;

import net.ion.framework.util.Debug;
import net.ion.repository.mongo.util.WriteJobs;

public class TestCollection extends TestBaseReset{

	public void testCollectionCount() throws Exception {
		session.tranSync(WriteJobs.dummy("/bleujin", 10)) ;
		assertEquals(11, session.collection().count()) ;
	}
}
