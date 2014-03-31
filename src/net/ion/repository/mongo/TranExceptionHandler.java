package net.ion.repository.mongo;


public interface TranExceptionHandler {

	public final static TranExceptionHandler PRINT = new TranExceptionHandler(){
		public void handle(WriteSession tsession, Throwable ex) {
			ex.printStackTrace() ;
		}

	} ;
		
	public void handle(WriteSession tsession, Throwable ex) ;

}
