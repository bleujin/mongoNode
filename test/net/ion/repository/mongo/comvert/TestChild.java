package net.ion.repository.mongo.comvert;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.comvert.sample.Dept;

public class TestChild extends TestBaseReset {

	public void testWhenHasChild() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/dept/dev").property("deptno", 20).property("name", "dev")
					.child("manager").property("name", "bleujin").property("age", 20).parent() 
					.child("address").property("city", "seoul").property("bun", 0) ;
				return null;
			}
		})  ;
		
		
		Dept dept = session.pathBy("/dept/dev").toBean(Dept.class) ;
		assertEquals(20, dept.deptNo()) ;
		assertEquals("dev", dept.name()) ;
		assertEquals("bleujin", dept.manager().name()) ;
		assertEquals(20, dept.manager().age()) ;
		
		assertEquals("seoul", dept.address().city()) ;
		assertEquals(0, dept.address().bun()) ;
	}
	
	public void testWhenHasGrandChild() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/dept/dev").property("deptno", 20).property("name", "dev")
					.child("address").property("city", "seoul").property("bun", 0).parent()
					.child("manager").property("name", "bleujin").property("age", 20)
						.child("pair").property("name", "hero").property("age", 30) ;
				return null;
			}
		}) ;
		
		Dept dept = session.pathBy("/dept/dev").toBean(Dept.class) ;
		assertEquals("hero", dept.manager().pair().name()) ;
		assertEquals(30, dept.manager().pair().age()) ;
		assertEquals(30, dept.manager().pair().age()) ;
	}
}

