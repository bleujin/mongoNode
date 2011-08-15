package net.ion.radon.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.impl.util.DebugPrinter;
import net.ion.radon.repository.ReferenceQuery.Forward;
import net.ion.radon.repository.myapi.ICursor;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

public class ReferenceTaragetCursor implements ICursor{

	private Repository repository ;
	private NodeCursor cursor ;
	private Forward forward ;
	
	private ReferenceTaragetCursor(Repository repository, NodeCursor ncursor, Forward forward) {
		this.repository = repository ;
		this.cursor = ncursor ;
		this.forward = forward ;
	}

	public static ReferenceTaragetCursor create(Repository repository, NodeCursor ncursor, Forward forward) {
		return new ReferenceTaragetCursor(repository, ncursor, forward);
	}

	public boolean hasNext() {
		return cursor.hasNext();
	}
	public Reference nextReference() {
		return Reference.create(repository, cursor.next()) ;
	}
	
	public Node next() {
		Reference nextReference = nextReference();
		NodeImpl result = forward == Forward.To ? (NodeImpl) nextReference.getTargetNode() : (NodeImpl) nextReference.getSourceNode() ;
		// result._put("_relation_type", nextReference.getType()) ;
		return result ;
	}

	public int count() {
		return cursor.count();
	}

	public ReferenceTaragetCursor skip(int n) {
		cursor.skip(n) ;
		return this;
	}

	public ReferenceTaragetCursor limit(int n) {
		cursor.limit(n) ;
		return this ;
	}

	public int size() {
		return cursor.size();
	}

	public List<Node> toList(PageBean page) {
		this.skip(page.getSkipScreenCount()).limit(page.getMaxScreenCount()+1);
		return toList(page.getPageIndexOnScreen() * page.getListNum(), page.getListNum()) ;
	}
	
	private List<Node> toList(int skip, int limit) {
		while(skip-- > 0){
			if (hasNext()) {
				next() ;	
			} else {
				return new ArrayList<Node>();
			}
		}
		
		List<Node> result = new ArrayList<Node>();
		while(limit-- > 0 && cursor.hasNext()) {
			result.add(next()) ;
		}
		
		return result;
	}

	public List<Map<String, ? extends Object>> toMapList(PageBean page) {
		List<Node> list = toList(page) ;

		List<Map<String, ?>> result = ListUtil.newList();
		for(Node node : list){
			result.add(node.toMap()) ;
		}
		return result;
	}

	public List<Map<String, ? extends Object>> toPropertiesList(PageBean page) {
		List<Node> list = toList(page) ;

		List<Map<String, ?>> result = new ArrayList<Map<String,?>>();
		for(Node node : list){
			result.add(node.toPropertyMap()) ;
		}
		return result;
	}
	
	public NodeScreen screen(PageBean page) {
		List<Node> pageNode = this.toList(page) ;	
		return NodeScreen.create(size(), pageNode, page) ;
	}


	public void debugPrint(PageBean page) {
		each(page, new DebugPrinter());
	}

	public void each(PageBean page, Closure closure) {
		CollectionUtils.forAllDo(toList(page), closure) ;
	}

	public List<Node> toList(PageBean page, PropertyComparator comparator) {
		List<Node> nodes = toList(page);
		
		Collections.sort(nodes, comparator);
		return nodes;
	}


}
