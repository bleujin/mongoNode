package net.ion.radon.repository.myapi;

import java.util.List;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.repository.Node;

public interface INodeScreen {

	public int getScreenSize() ;

	public List<Node> getPageNode()  ;

	public JsonObject getJSONObject() ;
}
