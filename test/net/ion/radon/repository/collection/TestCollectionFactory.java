package net.ion.radon.repository.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import junit.framework.TestCase;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

public class TestCollectionFactory extends TestCase {

	public void testCreate() throws Exception {
		RepositoryCentral rc = RepositoryCentral.testCreate();
		
		CollectionFactory cf = rc.colFactory("collection");
		
		Map<String, ? extends TestBean> map = cf.newConcurrentMap(String.class, TestBean.class) ;
		Set<TestBean> set = cf.newSet(TestBean.class) ;
		Queue<TestBean> queue = cf.newQueue(TestBean.class) ;
		
		MongoConcurrentMap<String, TestBean> backer = cf.newConcurrentMap(String.class, TestBean.class);
		CachingConcurrentMap<String, TestBean> cmap = cf.newCacheConcurrentMap(backer);
		
	}
	
	
	
}

class Person {
	
	private long empno ;
	private int age ;
	private String name ;
	private Address address ;
	
	
	
	public long getEmpno() {
		return empno;
	}
	public void setEmpno(long empno) {
		this.empno = empno;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public static Person create(long empno, String name, int age, Address address) {
		Person newPerson = new Person();
		newPerson.setEmpno(empno) ;
		newPerson.setAge(age) ;
		newPerson.setName(name) ;
		newPerson.setAddress(address) ;
		return newPerson;
	}
	
	
	
}

class Address {
	private String city ;
	
	public final static Address create(String city){
		Address newAddress = new Address();
		newAddress.setCity(city) ;
		return newAddress ;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	
}


class TestBean {
    
    protected String name;
    protected int count;
    protected byte[] data;
    protected TestBean next;
    protected List<TestBean> beans = new ArrayList<TestBean>();
    
    public TestBean() {
    }
    
    public TestBean(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        return name.equals(((TestBean) obj).name);
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public TestBean getNext() {
        return next;
    }
    
    public void setNext(TestBean next) {
        this.next = next;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public List<TestBean> getBeans() {
        return beans;
    }
    
    public void setBeans(List<TestBean> beans) {
        this.beans = beans;
    }
    
}