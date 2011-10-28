package net.ion.radon.repository;

import net.ion.framework.util.StringUtil;
public class ReferenceQuery {

	public enum Forward {
		From, To ;
		
		String targetString(){
			if (this == From){
				return "target" ;
			} else if ( this == To){
				return "src" ;
			} else {
				throw new IllegalArgumentException("unsupported.forward") ;
			}
		}
	}
	
	private PropertyQuery query = PropertyQuery.create();
	private Forward forward = Forward.From ;
	private ReferenceManager refManager;
	
	private ReferenceQuery(ReferenceManager refManager){
		this.refManager = refManager;
	}
	
	public static ReferenceQuery create(ReferenceManager refManager){
		return new ReferenceQuery(refManager);
	}
	
	public ReferenceTaragetCursor find() {
		return refManager.find(this);
	}
	
	public Node findOne() {
		return find().next();
	}

	public NodeResult remove() {
		return refManager.remove(this);
	}
	
	
	public PropertyQuery getQuery(){
		return query ;
	}
	
	public Forward getForward(){
		return forward ;
	}
	
	public ReferenceQuery from(Node src){
		this.query = PropertyQuery.create(ReferenceManager.SRC, src.toRef());
		this.forward = Forward.To;
		return this;
	}

	
	private void addQuery(String key, Object value){
		query.put(key, value) ;
	}

	public ReferenceQuery from(Node src, String refType, Node target){
		from(src, refType);
		addQuery(ReferenceManager.TARGET, target.toRef());
		return this;
	}

	public ReferenceQuery to(Node target){
		this.query = PropertyQuery.create(ReferenceManager.TARGET, target.toRef());
		this.forward = Forward.From;
		return this;
	}
	

	public ReferenceQuery from(Node src, String refType){
		from(src);
		addReferenceQuery(refType, Forward.From) ;
		return this;
	}

	public ReferenceQuery to(Node target, String refType){
		to(target);
		addReferenceQuery(refType, Forward.To) ;
		return this;
	}

	private void addReferenceQuery(String _refType, Forward about) {
		
		String fullTypeExpression =   _refType.startsWith(":") ? _refType : ":reference:" + _refType;
		
		final String refType = StringUtil.substringBetween(fullTypeExpression, ":", ":").toLowerCase();
		final String findValue = StringUtil.substringAfterLast(fullTypeExpression, ":");
		
		if ("reference".equals(refType)) {
			addQuery(ReferenceManager.TYPE, findValue) ;
		} else if ("aradon".equals(refType)) {
			addQuery(about.targetString() + "_aradon.group", findValue) ;
		} else if ("workspace".equals(refType)) {
			addQuery(about.targetString() + ".workspace", findValue) ;
		} else {
			throw new IllegalArgumentException("undefined search ref type : " + refType) ;
		}
		
	}
	
	public ReferenceQuery child(Node src){
		return from(src, "_child");
	}
	
	public ReferenceQuery parent(Node src){
		return to(src,  "_child");
	}

	public ReferenceQuery child(Node src, String cname) {
		from(src, "_child");
		addQuery("name", cname);
		return this;
	}

	public String toString(){
		return "query:" + query + ", forward:" + forward ; 
	}

	public ReferenceQuery ascending(String... keys) {
		return null;
	}



}
