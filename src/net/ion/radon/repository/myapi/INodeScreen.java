package net.ion.radon.repository.myapi;

import java.util.List;

import net.ion.radon.repository.Node;
import net.sf.json.JSONObject;

public interface INodeScreen {

	public int getScreenSize() ;

	public List<Node> getPageNode()  ;

	public JSONObject getJSONObject() ;
}
