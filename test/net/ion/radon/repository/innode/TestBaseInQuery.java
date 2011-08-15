package net.ion.radon.repository.innode;

import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;
import net.sf.json.JSONObject;

public class TestBaseInQuery extends TestBaseRepository{
	
	protected Node makeNode() {
		Node node = session.newNode() ;
		node.put("people", makeSampleJSON()) ;
		return node;
	}
	protected JSONObject makeSampleJSON(int i) {
		JSONObject result = makeSampleJSON();
		result.put("index", i) ;
		return result;
	}

	protected JSONObject makeSampleJSON() {
		return JSONObject.fromObject("{name:'bleujin',1:2, address:{city:'seoul',street:[1, 2, 3], col:{val:'A'}},color:['red','blue','white']}");
	}

	protected Node createNode() {
		Node node = session.newNode() ;
		for (int i = 0; i < 5; i++) {
			JSONObject jo = makeSampleJSON(i) ;
			node.inner("people").append(jo) ;
		}
		session.commit() ;
		return node ;
	}
	

}
