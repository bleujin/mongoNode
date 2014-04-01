package net.ion.repository.mongo.util;

import java.util.List;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.node.ReadChildrenEach;
import net.ion.repository.mongo.node.ReadChildrenIterator;
import net.ion.repository.mongo.node.ReadNode;

public class ReadChildrenEachs {

	public static final ReadChildrenEach<List<ReadNode>> LIST = new ReadChildrenEach<List<ReadNode>>() {
		@Override
		public List<ReadNode> handle(ReadChildrenIterator citer) {
			List<ReadNode> result = ListUtil.newList() ;
			while(citer.hasNext()){
				result.add(citer.next()) ;
			}
			return result;
		}
	};
	public static final ReadChildrenEach<Void> DEBUG = new ReadChildrenEach<Void>(){
		@Override
		public Void handle(ReadChildrenIterator citer) {
			while(citer.hasNext()){
				Debug.debug(citer.next()) ;
			}
			return null;
		}
	};
	public static final ReadChildrenEach<Set<String>> CHILDREN_NAME = new ReadChildrenEach<Set<String>>(){
		@Override
		public Set<String> handle(ReadChildrenIterator citer) {
			Set result = SetUtil.newSet() ;
			while(citer.hasNext()){
				result.add(citer.next().fqn().name()) ;
			}
			return result;
		}
	};

}
