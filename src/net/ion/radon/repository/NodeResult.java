package net.ion.radon.repository;

import com.mongodb.WriteResult;

public class NodeResult {

	public static final NodeResult NULL = new EmptyResult();
	
	private PropertyQuery query ;
	private WriteResult wr ;
	
	private NodeResult(PropertyQuery query, WriteResult wr) {
		this.query = query ;
		this.wr = wr ; 
	}

	public static NodeResult create(PropertyQuery query, WriteResult wr) {
		return new NodeResult(query, wr);
	}

	public String getErrorMessage(){
		return wr.getError() ;
	}

	public int getRowCount(){
		return wr.getN() ;
	}

	public String toString(){
		return wr.toString() ;
	}
	
	public boolean isLazy(){
		return wr.isLazy() ;
	}
	
	public PropertyQuery getQuery(){
		return query ;
	}

	private static class EmptyResult extends NodeResult {
		
		EmptyResult(){
			super(PropertyQuery.EMPTY, null) ;
		}
		public String getErrorMessage(){
			return "";
		}

		public int getRowCount(){
			return -1 ;
		}

		public String toString(){
			return "" ;
		}
		
		public boolean isLazy(){
			return false ;
		}
	}
}

