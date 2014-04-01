package net.ion.repository.mongo.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.node.ReadChildrenEach;
import net.ion.repository.mongo.node.ReadChildrenIterator;
import net.ion.repository.mongo.node.ReadNode;

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
