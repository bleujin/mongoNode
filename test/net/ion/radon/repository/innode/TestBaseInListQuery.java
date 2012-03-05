package net.ion.radon.repository.innode;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;

public class TestBaseInListQuery extends TestBaseRepository{
	
	protected Node makeNode() {
		Node node = session.newNode() ;
		node.put("people", makeSampleJSON()) ;
		return node;
	}
	protected JsonObject makeSampleJSON(int i) {
		JsonObject result = makeSampleJSON();
		result.put("index", i) ;
		return result;
	}

	protected JsonObject makeSampleJSON() {
		return JsonParser.fromString("{name:'bleujin',1:2, address:{city:'seoul',street:[1, 2, 3], col:{val:'A'}},color:['red','blue','white']}").getAsJsonObject();
	}

	protected Node createNode() {
		Node node = session.newNode() ;
		for (int i = 0; i < 5; i++) {
			JsonObject jo = makeSampleJSON(i) ;
			node.inlist("people").push(jo.toMap()) ;
		}
		session.commit() ;
		return node ;
	}
	
	protected int createNodes() {
		for (int p = 0; p < 3; p++) {
			Node node = session.newNode() ;
			node.put("oindex", p) ;
			for (int i = 0; i < 5; i++) {
				JsonObject jo = makeSampleJSON(i) ;
				node.inlist("people").push(jo.toMap()) ;
			}
		}
		return session.commit() ;
	}
	

}
