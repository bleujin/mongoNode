package net.ion.radon.repository;

import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;

public class ReferenceManager {
	
	final static String REFERENCE_WORKSPACE = "_reference";
	private Repository repository;
	
	public final static String SRC = "src" ;
	public final static String TARGET = "target";
	public final static String TYPE = "type";

	public final static String SRC_ARADON = "src_aradon" ;
	public final static String TARGET_ARADON = "target_aradon" ;

	
	
	final static char[] NOT_CHAR = new char[] { ':', '[', ']', '/', '{', '}', '*' };

	private ReferenceManager(Repository repository) {
		this.repository = repository;
	}

	public static ReferenceManager create(Repository repository) {
		return new ReferenceManager(repository);
	}

	
	ReferenceTaragetCursor find(ReferenceQuery rquery) {
		return ReferenceTaragetCursor.create(repository, getWorkspace().find(rquery.getQuery(), Columns.ALL), rquery.getForward());
	}
	
	NodeResult remove(ReferenceQuery rquery) {
		return getWorkspace().removeQuery(rquery.getQuery())  ;
	}


	private Workspace getWorkspace() {
		return repository.getWorkspace(REFERENCE_WORKSPACE);
	}
	

	Node addReference(Node src, String relType, Node target) {
		createRefNode(src, relType, target);

		return target ;
	}

	private Node createRefNode(Node src, String relType, Node target) {
		Node newNode = getWorkspace().newNode() ;
		newNode.put(SRC, src.selfRef());
		newNode.put(TYPE, relType.toLowerCase());
		newNode.put(TARGET, target.selfRef());
		
		newNode.put(SRC_ARADON, src.getAradonId()) ;
		newNode.put(TARGET_ARADON, target.getAradonId()) ;
		return newNode ;
	}

	Node addChildReference(Node src, String name, Node target) {
		if (StringUtil.indexOfAny(name, NOT_CHAR) > -1 || StringUtil.isBlank(name))
			throw new IllegalArgumentException("not permitted char[" + NOT_CHAR.toString() + "] :" + name);

		Node node = createRefNode(src, "_child", target) ;
		node.put("name", name) ;
		return node ;
	}

	
	List<Node> getDesendantReference(Node src){
		List<Node> child = ReferenceQuery.create(this).child(src).find().toList(PageBean.ALL);
		
		List<Node> desendant = new ArrayList<Node>() ;
		desendant.addAll(child) ;
		
		for (Node node : child) {
			desendant.addAll(getDesendantReference(node));
		}
		return desendant ;
	}
	
	List<Node> removeDescendant(Node node) {
		return removeNodes(getDesendantReference(node));
	}

	private List<Node> removeNodes(List<Node> children) {
		for (Node child : children) {
			repository.getWorkspace(child.getWorkspaceName()).remove(child) ;
		}

		return children ;
	}

	List<Node> removeChildNode(Node src, String nameOrId) {
		// remove target node
		if (isId(nameOrId)) {
			Node target = repository.findNodeById(StringUtil.substringBetween(nameOrId, "[", "]")) ;
			repository.getWorkspace(target.getWorkspaceName()).remove(target) ;
			
			return ListUtil.toList(target) ;
		} else {
			return removeNodes(ReferenceQuery.create(this).child(src, nameOrId).find().toList(PageBean.ALL));
		}
	}
	
	public List<Node> removeChildNodes(NodeImpl src) {
		return removeNodes( ReferenceQuery.create(this).child(src).find().toList(PageBean.ALL));
	}

	private boolean isId(String nameOrId) {
		return nameOrId.startsWith("[");
	}

	void removeAboutReference(Node node) {
		ReferenceQuery.create(this).from(node).remove();
		ReferenceQuery.create(this).to(node).remove();
//		getWorkspace().removeQuery(ReferenceQuery.from(node).getQuery());
//		getWorkspace().removeQuery(ReferenceQuery.to(node).getQuery());

	}

	public void reset() {
		getWorkspace().drop() ;
	}

	
	
//	public ReferenceTaragetCursor from(Node src) {
//		return find(ReferenceQuery.from(src));
//	}
//
//	public ReferenceTaragetCursor from(Node src, String refType) {
//		return find(ReferenceQuery.from(src, refType));
//	}
//
//	public ReferenceTaragetCursor to(Node target) {
//		return find(ReferenceQuery.to(target));
//	}
//
//	public ReferenceTaragetCursor to(Node target, String refType) {
//		return find(ReferenceQuery.to(target, refType));
//	}
//
//	public ReferenceTaragetCursor parent(Node src) {
//		return find(ReferenceQuery.parent(src));
//	}
//
//	public ReferenceTaragetCursor child(Node src, String cname) {
//		return find(ReferenceQuery.child(src, cname));
//	}
//
//	public ReferenceTaragetCursor child(Node src) {
//		return find(ReferenceQuery.child(src));
//	}
//
//	public ReferenceTaragetCursor from(Node src, String refType, Node target) {
//		return find(ReferenceQuery.from(src, refType, target));
//	}


}
