package net.ion.radon.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ChainMap;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.mr.ReduceFormat;

public interface SessionQuery extends Serializable {

	public NodeCursor find() throws RepositoryException;

	public NodeCursor find(Columns columns) throws RepositoryException;

	public Node findOne() throws RepositoryException;

	public Node findOne(Columns columns);

	public <T> T findOne(Class<T> clz);

	public boolean existNode();

	public int remove();

	public SessionQuery aradonGroup(String groupId);

	public SessionQuery aradonGroupId(String groupId, Object uId);

	public SessionQuery path(String path);

	public SessionQuery eq(String key, Object value);

	public SessionQuery in(String key, Object[] objects);

	public SessionQuery nin(String key, Object[] objects);

	public SessionQuery and(PropertyQuery... conds);

	public SessionQuery or(PropertyQuery... conds);

	public SessionQuery ne(String key, String value);

	public SessionQuery between(String key, Object open, Object close);

	public SessionQuery where(String where);

	public SessionQuery gte(String key, Object value);

	public SessionQuery lte(String key, Object value);

	public SessionQuery eleMatch(String key, PropertyQuery eleQuery);

	public SessionQuery isExist(String key);

	public SessionQuery isNotExist(String key);

	public SessionQuery gt(String key, Object value);

	public SessionQuery lt(String key, Object value);

	public SessionQuery to(Node target, String relType);

	public List<Node> find(PageBean page) throws RepositoryException;

	public SessionQuery ascending(String... propIds);

	public SessionQuery descending(String... propIds);

	public String toString();

	public SessionQuery startPathInclude(String path);

	public SessionQuery regEx(String key, String regValue);

	public SessionQuery id(String oid);

	public SessionQuery idIn(String[] oids);

	public SessionQuery aquery(String str);

	public int count();

	// map에 없는 key의 property들은 지워짐
	public boolean overwriteOne(Map<String, ?> map);

	// map에 있는 값들만 set, map에 없는 key의 property들은 남아 있음.
	public boolean updateOne(Map<String, ?> map);

	public PropertyQuery getQuery();

	public NodeResult update(ChainMap modValues);

	public NodeResult update(Map<String, ?> modValues);

	public NodeResult merge(ChainMap modValues);

	public NodeResult merge(Map<String, ?> modValues);

	// upset = true, 즉 query에 해당하는 row가 없으면 새로 만든다.
	public NodeResult increase(String propId);

	// upset = true, 즉 query에 해당하는 row가 없으면 새로 만든다.
	public NodeResult increase(String propId, int incvalue);

	public Node findOneInDB();

	public InListQueryNode inlist(String field);

	public NodeCursor format(ReduceFormat format);

	public NodeCursor mapreduce(String mapFunction, String reduceFunction, String finalFunction);

	public NodeCursor mapreduce(String mapFunction, String reduceFunction, String finalFunction, CommandOption options);

	public Object apply(String mapFunction, String reduceFunction, String finalFunction, CommandOption options, ApplyHander handler);

	public NodeCursor group(IPropertyFamily keys, IPropertyFamily initial, String reduce);

	public UpdateChain updateChain();

}
