package net.ion.radon.repository.orm;

import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.TestBaseRepository;

public class TestEmployee extends TestBaseRepository {

	private EmployeeManager<Employee> em;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		em = new EmployeeManager<Employee>(session);
		em.removeAll();
	}

	public void testCreate() throws Exception {
		em.save(Employee.create(7756, "james", 1970, "seoul")) ;
		
		Employee emp = em.findById(7756) ;
		assertEquals(7756, emp.getEmpNo()) ;
		assertEquals("james", emp.getName()) ;
		assertEquals(1970, emp.getBirthYear()) ;
	}

	public void testComposite() throws Exception {
		PeopleManager<People> pm = new PeopleManager<People>(session) ;
		pm.removeAll() ;
		
		em.save(Employee.create(7756, "james", 1970, "seoul")) ;
		pm.save(People.create("bleujin", 20, "seoul", "red")) ;
		
		assertEquals(1, pm.find(PropertyQuery.create()).count()) ;
		assertEquals(1, em.find(PropertyQuery.create()).count()) ;

		assertEquals("seoul", pm.findById("bleujin").getAddress()) ;
		assertEquals("seoul", em.findById(7756).getAddress()) ;

		assertEquals(true, pm.findById("notfound") == null) ;
		assertEquals(true, em.findById("bleujin") == null) ;
	}

	
	
	
}
