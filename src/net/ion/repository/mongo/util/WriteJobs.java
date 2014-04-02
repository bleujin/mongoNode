package net.ion.repository.mongo.util;

import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;

public class WriteJobs {

	public static final WriteJob<Void> HELLO = new WriteJob<Void>(){
		@Override
		public Void handle(WriteSession wsession) {
			wsession.pathBy("/bleujin").property("name", "bleujin").property("greeting", "hello") ;
			return null;
		}
	};

	public static WriteJob<Void> dummy(final String prefix, final int count){
		return new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) {
				for (int i = 0; i < count; i++) {
					wsession.pathBy(prefix + "/" + i).property("dummy", i).property("name", "dummy") ;
				}
				return null;
			}
		} ;
	}
	

}
