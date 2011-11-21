package net.ion.radon.repository.orm;



public final class People extends AbstractORM{

	private static final long serialVersionUID = -2889183273379267256L;
	private final static String keyPropId = "userId";

	public People(){} ;
	
	public static People create(String id, int age, String address, String fcolor) {
		People newPeople = new People() ;
		newPeople.setUserId(id) ;
		newPeople.setAge(age) ;
		newPeople.setAddress(address) ;
		newPeople.setFavoriateColor(fcolor) ;
		
		return newPeople ;
	}
		
	public void setUserId(String id) {
		put(keyPropId, id) ;
	}


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
		return getAsInt("age") ;
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
