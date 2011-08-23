package net.ion.radon.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.innode.AndFilter;
import net.ion.radon.repository.innode.BetweenFilter;
import net.ion.radon.repository.innode.EqualFilter;
import net.ion.radon.repository.innode.ExistFilter;
import net.ion.radon.repository.innode.GreaterFilter;
import net.ion.radon.repository.innode.GreaterThanFilter;
import net.ion.radon.repository.innode.InFilter;
import net.ion.radon.repository.innode.InNodeFilter;
import net.ion.radon.repository.innode.LessFilter;
import net.ion.radon.repository.innode.LessThanFilter;
import net.ion.radon.repository.innode.NodeSort;
import net.ion.radon.repository.innode.NotEqualFilter;
import net.ion.radon.repository.innode.NotExistFilter;
import net.ion.radon.repository.innode.NotInFilter;
import net.ion.radon.repository.innode.OrFilter;

import org.bson.types.BasicBSONList;

import com.mongodb.DBObject;

public class InListQuery {

	private final NodeObject nobject;
	private final String pname ;
	private final Node parent ;

	private List<InNodeFilter> filters = ListUtil.newList() ;
	private List<NodeSort> sorts = ListUtil.newList() ;
	private Map<String, Object> modValues = MapUtil.newMap() ;
	
	private InListQuery(NodeObject nobject, String pname, Node parent) {
		this.nobject = nobject ;
		this.pname = pname ;
		this.parent = parent ;
	}
	
	public InListQuery eq(String path, Object value) {
		return addFilter(EqualFilter.create(path, value));
	}
	public InListQuery ne(String path, Object value) {
		return addFilter(NotEqualFilter.create(path, value));
	}

	public InListQuery gt(String path, Object value) {
		return addFilter(GreaterFilter.create(path, value));
	}
	public InListQuery gte(String path, Object value) {
		return addFilter(GreaterThanFilter.create(path, value));
	}
	public InListQuery lt(String path, Object value) {
		return addFilter(LessFilter.create(path, value));
	}
	public InListQuery lte(String path, Object value) {
		return addFilter(LessThanFilter.create(path, value));
	}
	public InListQuery between(String path, Object from, Object to) {
		return addFilter(BetweenFilter.create(path, from, to));
	}
	public InListQuery in(String path, Object[] values) {
		return addFilter(InFilter.create(path, values));
	}
	public InListQuery nin(String path, Object[] values) {
		return addFilter(NotInFilter.create(path, values));
	}
	public InListQuery exist(String path) {
		return addFilter(ExistFilter.create(path));
	}
	public InListQuery notExist(String path) {
		return addFilter(NotExistFilter.create(path));
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

	public InListQuery put(String path, Object value) {
		modValues.put(path, value) ;
		return this;
	}

	
	
	static InListQuery create(NodeObject nobject, String pname, Node parent) {
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
			boolean result = list.remove(inode.getDBObject()) ;
			if (result) count++ ;
		}
		parent.getSession().notify(parent, NodeEvent.UPDATE);
		return count ;
	}

	public int update() {
		List<InNode> founds = find() ;
		for (InNode inode : founds) {
			for (Entry<String, Object> entry : modValues.entrySet()) {
				inode.put(entry.getKey(), entry.getValue()) ;
			}
		}
		parent.getSession().notify(parent, NodeEvent.UPDATE);
		modValues.clear() ;
		return founds.size() ;
	}

	

	
	
	
	
	
	private List<InNode> myfind(PageBean page){
		List<InNode> result = ListUtil.newList() ;
		
		for (DBObject dbo : ((BasicBSONList)nobject.getDBObject()).toArray(new DBObject[0])) {
			NodeObject no = NodeObject.load(dbo) ;
			boolean isSatisfy = true ;
			for(InNodeFilter filter : filters){
				if (! filter.isTrue(no)) {
					isSatisfy = false ;
					break ;
				} 
			}
			if (isSatisfy) result.add(InNodeImpl.create(no, pname, parent)) ;
		}
		
		return sort(result, page) ;
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
