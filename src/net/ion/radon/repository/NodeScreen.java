package net.ion.radon.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonString;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.radon.core.PageBean;

public class NodeScreen implements JsonString{

	
	private final int screenSize ;
	private final List<Node> pageNode ;
	private final PageBean page ;
	
	public NodeScreen(int screenSize, List<Node> pageNode, PageBean page) {
		this.screenSize = screenSize ;
		this.pageNode = Collections.unmodifiableList(pageNode) ;
		this.page = page ;
	}

	public static NodeScreen create(int screenSize, List<Node> pageNode, PageBean page) {
		return new NodeScreen(screenSize, pageNode, page) ;
	}

	public int getScreenSize() {
		return screenSize;
	}

	public List<Node> getPageNode() {
		return pageNode;
	}

	public List<Map<String, ? extends Object>> getPageMap() {
		List<Map<String, ?>> result = new ArrayList<Map<String, ?>>() ;
		for (Node node : getPageNode()) {
			result.add(node.toPropertyMap()) ;
		}
		return result;
	}

	public JsonObject getJSONObject() {
		List<Node> pageResult = getPageNode();
		
		JsonArray pageRows = new JsonArray() ;
		for (Node row : pageResult) {
			pageRows.add(JsonUtil.toProperElement(row.toString())) ;
		}

		JsonObject result = new JsonObject() ;
		JsonObject request = new JsonObject() ;
		request.accumulate("page", page.toString()) ;
		JsonObject response = new JsonObject() ;
		response.accumulate("totalCount", getScreenSize()) ;
		
		result.add("nodes", pageRows) ;
		result.add("request", request) ;
		result.add("response", response) ;
		return result;
	}

	public String toJsonString() {
		return getJSONObject().toString();
	}
	
}
