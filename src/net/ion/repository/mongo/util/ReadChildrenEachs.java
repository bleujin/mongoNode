package net.ion.repository.mongo.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.node.IteratorList;
import net.ion.repository.mongo.node.ReadChildren;
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
				ReadNode next = citer.next();
				Debug.debug(next.fqn(), next.transformer(Transformers.READ_TOFLATMAP)) ;
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
	
	public static final ReadChildrenEach<Integer> COUNT = new ReadChildrenEach<Integer>(){
		@Override
		public Integer handle(ReadChildrenIterator citer) {
			return citer.count();
		}
	};

	public static final ReadChildrenEach<ReadNode> FIRSTNODE = new ReadChildrenEach<ReadNode>() {
		@Override
		public ReadNode handle(ReadChildrenIterator citer) {
			return citer.hasNext() ? citer.next() : null;
		}
	};
	public static ReadChildrenEach<IteratorList<ReadNode>> ITERATOR = new ReadChildrenEach<IteratorList<ReadNode>>() {

		@Override
		public IteratorList<ReadNode> handle(final ReadChildrenIterator citer) {
			return new IteratorList<ReadNode>() {
				
				@Override
				public Iterator<ReadNode> iterator() {
					return citer;
				}
				
				@Override
				public ReadNode next() {
					return citer.next();
				}
				
				@Override
				public boolean hasNext() {
					return citer.hasNext();
				}
				
				@Override
				public List<ReadNode> toList() {
					List<ReadNode> result = ListUtil.newList() ;
					while(hasNext()){
						result.add(citer.next()) ;
					}
					return result;
				}
			};
		}
	};
	
}
