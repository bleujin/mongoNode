package net.ion.repository.mongo.comvert;

import java.util.Date;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.comvert.sample.Dept;
import net.ion.repository.mongo.node.ReadNode;

public class TestToChildBean extends TestBaseReset{

	public void testIncludeChildBean() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().child("/dev").property("name", "dev").property("deptno", 20)
					.child("manager").property("name", "bleujin").property("created", new Date()) ;
				return null ;
			}
		}) ;
		
		ReadNode dev = session.pathBy("/dev");
		final Dept devBean = dev.toBean(Dept.class);
		assertEquals("dev", devBean.name()) ;
		assertEquals(20, devBean.deptNo()) ;
		
		assertEquals("bleujin", devBean.manager().name()) ;
	}
	
	
	
}

