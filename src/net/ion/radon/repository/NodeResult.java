package net.ion.radon.repository;

import com.mongodb.WriteResult;

public class NodeResult {

	public static final NodeResult NULL = new EmptyResult();
	
	private WriteResult wr ;
	
	private NodeResult(WriteResult wr) {
		this.wr = wr ; 
	}

	public static NodeResult create(WriteResult wr) {
		return new NodeResult(wr);
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
	
	private static class EmptyResult extends NodeResult {
		
		EmptyResult(){
			super(null) ;
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

