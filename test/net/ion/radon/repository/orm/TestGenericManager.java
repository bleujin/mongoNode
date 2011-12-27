package net.ion.radon.repository.orm;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.orm.bean.People;
import net.ion.radon.repository.orm.manager.PeopleManager;

public class TestGenericManager extends TestBaseRepository{

	public void testInherit() throws Exception {
		PeopleManager pm = new PeopleManager(session) ;
		pm.createQuery().remove() ;
		
		People p = pm.loadInstance("bleujin") ;
		p.save() ;
		
		assertEquals(1, pm.createQuery().find().count()) ;
	}
	
	public void testFactory() throws Exception {
		GenericManager<People> pgm = GenericManager.create(session, People.class) ;
		pgm.createQuery().remove() ;
		
		People p = pgm.loadInstance("bleujin") ;
		p.save() ;
		
		Debug.line(pgm.createQuery().find().next()) ;
	}
}
