package net.ion.radon.repository.orm;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.orm.bean.Employee;
import net.ion.radon.repository.orm.bean.People;
import net.ion.radon.repository.orm.manager.PeopleManager;

public class TestEmployee extends TestBaseRepository {

	public void testCreate() throws Exception {
		GenericManager<Employee> em = GenericManager.create(session, Employee.class);
		em.createQuery().remove();
		
		Employee james = em.loadInstance(7756) ;
		james.setName("james") ;
		james.setBirthYear(1970) ;
		james.setAddress("seoul") ;
		NodeResult nr = james.save() ;
		assertEquals(1, nr.getRowCount()) ;
		
		Employee emp = em.findById(7756) ;
		assertEquals(true, emp != null) ;
		assertEquals(7756, emp.getEmpNo()) ;
		assertEquals("james", emp.getName()) ;
		assertEquals(1970, emp.getBirthYear()) ;
	}

	public void testComposite() throws Exception {
		GenericManager<Employee> em = GenericManager.create(session, Employee.class);
		em.createQuery().remove();

		PeopleManager pm = new PeopleManager(session) ;
		pm.createQuery().remove() ;

		Employee emp = em.loadInstance(7756) ;
		emp.setName("james") ;
		emp.setBirthYear(1970) ;
		emp.setAddress("seoul") ;
		emp.save() ;
		
		People people = pm.loadInstance("bleujin") ;
		people.setAge(20) ;
		people.setAddress("seoul") ;
		people.setFavoriateColor("red") ;
		people.save() ;
		
		assertEquals(1, pm.createQuery().find().count()) ;
		assertEquals(1, em.createQuery().find().count()) ;

		assertEquals("seoul", pm.findById("bleujin").getAddress()) ;
		assertEquals("seoul", em.findById(7756).getAddress()) ;

		assertEquals(true, pm.findById("notfound") == null) ;
		assertEquals(true, em.findById("bleujin") == null) ;
	}

	
	
	
}
