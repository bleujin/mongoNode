package net.ion.radon.repository.orm;

import java.lang.reflect.Constructor;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.orm.bean.Employee;

import org.apache.commons.beanutils.ConstructorUtils;

import junit.framework.TestCase;

public class TestConstructor extends TestBaseRepository {

	public void testPrivateException() throws Exception {
		try {
			Object obj = ConstructorUtils.invokeConstructor(MyObject.class, new Object[0]) ;
			fail() ;
		} catch(NoSuchMethodException expect){}
	}
	
	public void testCreatePrivate() throws Exception {
		Constructor<MyObject> con = MyObject.class.getDeclaredConstructor(String.class) ;
		con.setAccessible(true) ;
		MyObject obj =  con.newInstance(123) ;
		Debug.line(obj) ;
	}
	
	public void testFind() throws Exception {
		Constructor<MyObject>[] cons = MyObject.class.getDeclaredConstructors() ;
	
		Object i = 3 ;
		for (Constructor<MyObject> con : cons) {
			Debug.line(con, con.getParameterTypes()) ;
			if (con.getParameterTypes().length == 1){
				con.setAccessible(true) ;
				MyObject my = con.newInstance(i) ;
				assertEquals(3, my.getVal()) ; 
				
			}
		}
	}
	
	public void testCreate() throws Exception {
		GenericManager<Employee> gm = GenericManager.create(session, Employee.class) ;
		Employee emp = gm.loadInstance(7756) ;
		emp.setName("bleujin") ;
		
		Debug.line(emp) ;
	}
	
	
	
}

class MyObject {
	private int val ;
	private MyObject(int val){
		this.val = val ;
	}
	
	public int getVal(){
		return val ;
	}
	
	public String toString(){
		return String.valueOf(val) ;
	}
}
