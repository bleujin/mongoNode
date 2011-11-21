package net.ion.radon.repository.orm;

public class Employee extends AbstractORM{

	private static final long serialVersionUID = 2691323911911045867L;
	private final static String keyPropId = "empNo";

	public Employee(){} ;
	
	public static Employee create(int empNo, String name, int birthYear, String address) {
		Employee emp = new Employee() ;
		emp.setEmpNo(empNo) ;
		emp.setName(name) ;
		emp.setBirthYear(birthYear) ;
		emp.setAddress(address) ;
		
		return emp ;
	}
		
	public void setName(String name) {
		put("name", name) ;
	}

	public String getName() {
		return getString("name") ;
	}

	public void setEmpNo(int empNo) {
		put(keyPropId, empNo) ;
	}


	public int getEmpNo(){
		return super.getAsInt(keyPropId);
	}
	
	public void setAddress(String address) {
		put("address", address) ;
	}
	
	public String getAddress() {
		return getString("address") ;
	}
	
	public void setBirthYear(int birthYear) {
		put("birthyear", birthYear) ;
	}

	public int getBirthYear() {
		return getAsInt("birthyear") ;
	}
	
	public boolean equals(Object that){
		if (! (that instanceof Employee)){
			return false ;
		}
		
		return this.getEmpNo() == ((Employee)that).getEmpNo() ;
	}
	
	public int hashCode(){
		return getEmpNo();
	}
	
	public String toString(){
		return "empno:" + getEmpNo() + ", name:" + getName() ;
	}
}
