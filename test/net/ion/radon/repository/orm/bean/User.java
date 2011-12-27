package net.ion.radon.repository.orm.bean;

import net.ion.radon.repository.orm.NodeORM;
import net.ion.radon.repository.orm.InnerNodeORM;

public class User extends InnerNodeORM {

	public User() {
	}

	private static final long serialVersionUID = 2422243442236187680L;

	public static User create(String name, int age) {
		User user = new User() ;
		user.put("name", name) ;
		user.put("age", age) ;
		return user;
	}

	public String getUserName() {
		return getString("name");
	}

	public int getAge() {
		return getAsInt("age");
	}

}
