package net.ion.radon.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ion.radon.core.PageBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NodeScreen {

	
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

	public JSONObject getJSONObject() {
		List<Node> pageResult = getPageNode();
		
		JSONArray pageRows = new JSONArray() ;
		for (Node row : pageResult) {
			pageRows.add(row.toString()) ;
		}

		JSONObject result = new JSONObject() ;
		JSONObject request = new JSONObject() ;
		request.accumulate("page", page.toString()) ;
		JSONObject response = new JSONObject() ;
		response.accumulate("totalCount", getScreenSize()) ;
		
		result.accumulate("nodes", pageRows) ;
		result.accumulate("request", request.toString()) ;
		result.accumulate("response", response.toString()) ;
		return result;
	}
	
}
