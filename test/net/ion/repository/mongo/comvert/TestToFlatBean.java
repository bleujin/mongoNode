package net.ion.repository.mongo.comvert;

import java.util.Date;
import java.util.Set;

import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.comvert.sample.FlatPerson;
import net.ion.repository.mongo.node.ReadNode;

import org.apache.commons.lang.builder.ToStringBuilder;

public class TestToFlatBean extends TestBaseReset {

	public void testToFlatBean() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().child("/bleujin").property("name", "bleujin").property("age", 10).property("created", new Date()) ;
				return null ;
			}
		}) ;
		
		ReadNode bleujin = session.pathBy("/bleujin");
		assertEquals("bleujin", bleujin.toBean(FlatPerson.class).name()) ;
		assertEquals(new Date().getDate(), bleujin.toBean(FlatPerson.class).created().getDate()) ;
	}
	
	
	public void testAppend() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").append("name", "bleujin", "name", "jin") ;
				return null;
			}
		}) ;
		Person person = session.pathBy("/bleujin").toBean(Person.class);
		assertEquals(true, person.contains("bleujin")) ;
		assertEquals(true, person.contains("name")) ;
		assertEquals(true, person.contains("jin")) ;
		
	}
	
	
}

class Person {
	private Set<String> name ;
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this) ;
	}
	
	public boolean contains(String find){
		return name.contains(find) ;
	}
}
