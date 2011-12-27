package net.ion.radon.repository.orm.bean;

import net.ion.radon.repository.orm.NodeORM;
import net.ion.radon.repository.orm.IDMethod;


@IDMethod(workspaceName="peoples", groupId="people", keyPropId="userId", managerClz=People.class)
public final class People extends NodeORM{

	private static final long serialVersionUID = -2889183273379267256L;
	private final static String keyPropId = "userId";

	public People(String userId){
		super(userId) ;
	} ;
	
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
