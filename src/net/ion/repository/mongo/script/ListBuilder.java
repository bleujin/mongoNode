package net.ion.repository.mongo.script;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.util.ListUtil;
import net.ion.repository.mongo.node.ReadNode;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class ListBuilder extends AbstractBuilder {

    private List<BasicBuilder> list = ListUtil.newList();
    private BasicBuilder parent ;
    private BasicBuilder current ;
    protected ListBuilder(BasicBuilder parent) {
        this.parent = parent ;
    }

	public BasicBuilder parent() {
		return parent;
	}
    
    public ListBuilder next(){
		BasicBuilder created = new BasicBuilder(this) ;
		list.add(created) ;
		current = created ;
    	return this ;
    }
    
    public ListBuilder property(String name, Object value){
    	current.property(name, value) ;
    	return this ;
    }

	public ListBuilder property(ReadNode node, String values) {
		return (ListBuilder) super.property(node, values);
	}


	public AbstractBuilder property(Iterable<ReadNode> nodes, String values) {
		Iterator<ReadNode> iter = nodes.iterator() ;
		while(iter.hasNext()){
			ReadNode node = iter.next() ;
			property(node, values) ;
			if (iter.hasNext()) next() ;
		}
		return this;
	}

    
    @Override
    public JsonElement makeJson() {
        JsonArray array = new JsonArray();
        for(BasicBuilder b : list){
        	array.add(b.makeJson());
        }

        return array;
    }

}
