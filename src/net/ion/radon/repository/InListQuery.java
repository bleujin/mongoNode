package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.innode.AndFilter;
import net.ion.radon.repository.innode.BetweenFilter;
import net.ion.radon.repository.innode.EqualFilter;
import net.ion.radon.repository.innode.ExistFilter;
import net.ion.radon.repository.innode.GreaterFilter;
import net.ion.radon.repository.innode.GreaterThanFilter;
import net.ion.radon.repository.innode.InFilter;
import net.ion.radon.repository.innode.InNodeFilter;
import net.ion.radon.repository.innode.InNodeImpl;
import net.ion.radon.repository.innode.LessFilter;
import net.ion.radon.repository.innode.LessThanFilter;
import net.ion.radon.repository.innode.NodeSort;
import net.ion.radon.repository.innode.NotEqualFilter;
import net.ion.radon.repository.innode.NotExistFilter;
import net.ion.radon.repository.innode.NotInFilter;
import net.ion.radon.repository.innode.OrFilter;

import org.bson.types.BasicBSONList;

import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

public class InListQuery implements Serializable{

	private static final long serialVersionUID = -9117395018107563109L;
	private final NodeObject nobject;
	private final String pname ;
	private final INode parent ;

	private List<InNodeFilter> filters = ListUtil.newList() ;
	private List<NodeSort> sorts = ListUtil.newList() ;

	private InListQuery(NodeObject nobject, String pname, INode parent) {
		this.nobject = nobject ;
		this.pname = pname ;
		this.parent = parent ;
	}
	
	public InListQuery eq(String key, Object value) {
		return addFilter(EqualFilter.create(key, value));
	}
	public InListQuery ne(String key, Object value) {
		return addFilter(NotEqualFilter.create(key, value));
	}

	public InListQuery gt(String key, Object value) {
		return addFilter(GreaterFilter.create(key, value));
	}
	public InListQuery gte(String key, Object value) {
		return addFilter(GreaterThanFilter.create(key, value));
	}
	public InListQuery lt(String key, Object value) {
		return addFilter(LessFilter.create(key, value));
	}
	public InListQuery lte(String key, Object value) {
		return addFilter(LessThanFilter.create(key, value));
	}
	public InListQuery between(String key, Object from, Object to) {
		return addFilter(BetweenFilter.create(key, from, to));
	}
	public InListQuery in(String key, Object[] values) {
		return addFilter(InFilter.create(key, values));
	}
	public InListQuery nin(String key, Object[] values) {
		return addFilter(NotInFilter.create(key, values));
	}
	public InListQuery exist(String key) {
		return addFilter(ExistFilter.create(key));
	}
	public InListQuery notExist(String key) {
		return addFilter(NotExistFilter.create(key));
	}
	public InListQuery and(InNodeFilter... qs){
		return addFilter(AndFilter.create(qs)) ;
	}
	public InListQuery or(InNodeFilter... qs){
		return addFilter(OrFilter.create(qs)) ;
	}
	

	
	
	private InListQuery addFilter(InNodeFilter filter) {
		filters.add(filter);
		return this ;
	}

	
	public static InListQuery create(NodeObject nobject, String pname, INode parent) {
		return new InListQuery(nobject, pname, parent);
	}

	public InNode findOne() {
		List<InNode> result = myfind(PageBean.create(1,1)) ;
		if (result.size() >= 1) return result.get(0) ;
		return null ;
	}

	public List<InNode> find() {
		return myfind(PageBean.ALL) ;
	}
	
	public InListQuery addFilter(PropertyQuery query){
		DBObject dbo = query.getDBObject() ;
		addFilter(dbo);
		
		return this ;
	}

	private void addFilter(DBObject dbo) {
		Set<String> keys = dbo.keySet() ;
		
		for (String key : keys) {
			Object value = dbo.get(key) ;
			if (value instanceof DBObject){
				Map incons = ((DBObject)value).toMap() ;
				Set<String> inkeys = incons.keySet() ;
				for (String inkey : inkeys) {
					if (QueryOperators.GT.equals(inkey)){
						gt(key, incons.get(inkey)) ;
					} else if (QueryOperators.GTE.equals(inkey)){
						gte(key, incons.get(inkey)) ;
					} else if (QueryOperators.LT.equals(inkey)){
						lt(key, incons.get(inkey)) ;
					} else if (QueryOperators.LTE.equals(inkey)){
						lte(key, incons.get(inkey)) ;
					} else if (QueryOperators.EXISTS.equals(inkey)){
						if (incons.get(inkey).equals(Boolean.TRUE)) {
							exist(key) ; 
						} else {
							notExist(key);
						}
					} else if (QueryOperators.IN.equals(inkey)){
						in(key, ((List)incons.get(inkey)).toArray(new Object[0]) ) ;
					} else if (QueryOperators.NIN.equals(inkey)){
						nin(key, ((List)incons.get(inkey)).toArray(new Object[0])) ;
					}
				}
			} else {
				eq(key, value) ;
			}
		}
	}

	
	public List<InNode> find(PageBean page) {
		return myfind(page);
	}
	
	public InListQuery ascending(String path) {
		sorts.add(NodeSort.create(path, true)) ;
		return this ;
	}
	
	public InListQuery descending(final String path) {
		sorts.add(NodeSort.create(path, false)) ;
		return this ;
	}
	
	public int remove() {
		BasicBSONList list = (BasicBSONList)nobject.getDBObject() ;

		List<InNode> founds = find() ;
		int count = 0 ;
		for (InNode inode : founds) {
//			boolean result = list.remove(inode.getDBObject()) ;
//			if (result) count++ ;
			count += remove(inode.getDBObject().toMap());
		}
		parent.notify(NodeEvent.UPDATE);
		return count ;
	}
	
	public int remove(Map<String, ? extends Object> map) {
		BasicBSONList list = (BasicBSONList)nobject.getDBObject() ;

		int count = 0 ;
		boolean result = list.remove(map) ;
		if (result)
			count++ ;
		parent.notify(NodeEvent.UPDATE);
		return count ;
	}

	public int update(ChainMap cmap) {
		return update(cmap.toMap()) ;
	}
	
	public int update(Map<String, Object> modValues) {
		List<InNode> founds = find() ;
		for (InNode inode : founds) {
			for (Entry<String, Object> entry : modValues.entrySet()) {
				inode.put(entry.getKey(), entry.getValue()) ;
			}
		}
		
//		parent.getSession().createQuery().id(parent.getIdentifier()).inlist(this.pname).push(modValues) ;
		parent.notify(NodeEvent.UPDATE);
		return founds.size() ;
	}

	
	
	private List<InNode> myfind(PageBean page){
		List<InNode> result = ListUtil.newList() ;
		
		int index = 0 ;
		for (DBObject dbo : ((BasicBSONList)nobject.getDBObject()).toArray(new DBObject[0])) {
			NodeObject no = NodeObject.load(dbo) ;
			if (isTrue(filters, no)) result.add(InNodeImpl.create(dbo, pname, parent, index)) ;
			index++ ;
		}
		
		return sort(result, page) ;
	}
	
	
	private boolean isTrue(List<InNodeFilter> filters, NodeObject no){
		for(InNodeFilter filter : filters){
			if (! filter.isTrue(no)) {
				return false ;
			} 
		}
		return true ;
	}
	

	private List<InNode> sort(List<InNode> list, PageBean page) {
		
		final List<NodeSort> finalNodeSort = sorts ; 
		Collections.sort(list, new java.util.Comparator<INode>(){
			public int 	compare(INode n1, INode n2){
				for (NodeSort nsort : finalNodeSort) {
					int reuslt = sortCompare(n1, n2, nsort);
					if (reuslt != 0) return reuslt ;
				}
				return 0 ;
			}

			private int sortCompare(INode n1, INode n2, NodeSort nsort) {
				Object left =  n1.get(nsort.getPath()) ;
				Object right = n2.get(nsort.getPath()) ;
				
				if (left == null) return -1 ;
				if (right == null) return 1 ;// null first
				
				if (left instanceof Comparable && right instanceof Comparable) {
					return (nsort.isAscending() ? 1 : -1) * ((Comparable)left).compareTo((Comparable)right);
				} else {
					throw new IllegalStateException("not comparable type") ;
				}
			} 
		}) ;

		return page.subList(list);
	}


}
