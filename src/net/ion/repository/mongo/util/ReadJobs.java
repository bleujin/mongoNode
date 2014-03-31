package net.ion.repository.mongo.util;

import java.util.List;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.ReadJob;
import net.ion.repository.mongo.node.ChildrenIterator;
import net.ion.repository.mongo.node.ReadNode;

public class ReadJobs {

	public static final ReadJob<List<ReadNode>> LIST = new ReadJob<List<ReadNode>>() {
		@Override
		public List<ReadNode> handle(ChildrenIterator citer) {
			List<ReadNode> result = ListUtil.newList() ;
			while(citer.hasNext()){
				result.add(citer.next()) ;
			}
			return result;
		}
	};
	public static final ReadJob<Void> DEBUG = new ReadJob<Void>(){
		@Override
		public Void handle(ChildrenIterator citer) {
			while(citer.hasNext()){
				Debug.debug(citer.next()) ;
			}
			return null;
		}
	};
	public static final ReadJob<Set<String>> CHILDREN_NAME = new ReadJob<Set<String>>(){
		@Override
		public Set<String> handle(ChildrenIterator citer) {
			Set result = SetUtil.newSet() ;
			while(citer.hasNext()){
				result.add(citer.next().fqn().name()) ;
			}
			return result;
		}
	};

}
