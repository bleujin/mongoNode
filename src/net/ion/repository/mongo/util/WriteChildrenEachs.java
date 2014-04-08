package net.ion.repository.mongo.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.node.IteratorList;
import net.ion.repository.mongo.node.ReadChildrenEach;
import net.ion.repository.mongo.node.ReadChildrenIterator;
import net.ion.repository.mongo.node.ReadNode;
import net.ion.repository.mongo.node.WriteChildrenEach;
import net.ion.repository.mongo.node.WriteChildrenIterator;
import net.ion.repository.mongo.node.WriteNode;

public class WriteChildrenEachs {

	public static final WriteChildrenEach<List<WriteNode>> LIST = new WriteChildrenEach<List<WriteNode>>() {
		@Override
		public List<WriteNode> handle(WriteChildrenIterator citer) {
			List<WriteNode> result = ListUtil.newList() ;
			while(citer.hasNext()){
				result.add(citer.next()) ;
			}
			return result;
		}
	};
	public static final WriteChildrenEach<Void> DEBUG = new WriteChildrenEach<Void>(){
		@Override
		public Void handle(WriteChildrenIterator citer) {
			while(citer.hasNext()){
				WriteNode next = citer.next();
				Debug.debug(next.fqn(), next.transformer(Transformers.WRITE_TOFLATMAP)) ;
			}
			return null;
		}
	};
	
	public static final WriteChildrenEach<Set<String>> CHILDREN_NAME = new WriteChildrenEach<Set<String>>(){
		@Override
		public Set<String> handle(WriteChildrenIterator citer) {
			Set result = SetUtil.newSet() ;
			while(citer.hasNext()){
				result.add(citer.next().fqn().name()) ;
			}
			return result;
		}
	};
	
	public static final WriteChildrenEach<Integer> COUNT = new WriteChildrenEach<Integer>(){
		@Override
		public Integer handle(WriteChildrenIterator citer) {
			return citer.count();
		}
	};
	public static final WriteChildrenEach<IteratorList<WriteNode>> ITERATOR = new WriteChildrenEach<IteratorList<WriteNode>>() {

		@Override
		public IteratorList<WriteNode> handle(final WriteChildrenIterator citer) {
			return new IteratorList<WriteNode>() {
				
				@Override
				public Iterator<WriteNode> iterator() {
					return citer;
				}
				
				@Override
				public WriteNode next() {
					return citer.next();
				}
				
				@Override
				public boolean hasNext() {
					return citer.hasNext();
				}
				
				@Override
				public List<WriteNode> toList() {
					List<WriteNode> result = ListUtil.newList() ;
					while(hasNext()){
						result.add(citer.next()) ;
					}
					return result;
				}
			};
		}
	};

}
