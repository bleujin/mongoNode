package net.ion.radon.repository.orm;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;

public final class People extends AbstractORM{

	
	private final static String keyPropId = "userId";
	public People(){} ;
	
	public People(String userId) {
		super.put(keyPropId, userId) ;
	}
	
	public static People create(String id, int age, String address, String fcolor) {
		People newPeople = new People(id) ;
		newPeople.setAge(age) ;
		newPeople.setAddress(address) ;
		newPeople.setFavoriateColor(fcolor) ;
		
		return newPeople ;
	}
	

	
	@IDMethod(nodeName=keyPropId) 
	public String getId(){
		return super.getString(keyPropId);
	}
	
	public void setAddress(String address) {
		put("address", address) ;
	}
	
	public String getAddress() {
		return getString("address") ;
	}
	
	public void setFavoriateColor(String fcolor) {
		put("fcolor", fcolor) ;
	}
	
	public String getFavoriateColor() {
		return getString("fcolor") ;
	}

	
	public void setAge(int age) {
		put("age", age) ;
	}

	public int getAge() {
		return (Integer)get("age") ;
	}
	
	
	public boolean equals(Object that){
		if (! (that instanceof People)){
			return false ;
		}
		
		return this.getId().equals(((People)that).getId()) ;
	}
	
	public int hashCode(){
		return getId().hashCode() ;
	}
	
	public String toString(){
		return "id:" + getId() + ", age:" + getAge() ;
	}

}
