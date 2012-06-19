package net.ion.framework.util;

import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.RowSetMetaData;

import net.ion.framework.db.Rows;
import net.ion.framework.db.RowsImpl;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.db.rowset.RowSetMetaDataImpl;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;

public class MapListRows extends RowsImpl {

	private static final long serialVersionUID = 3216679219624572591L;
	private static Map<Class, Integer> TypeMappingMap = makeMapping();
	private static Map makeMapping() {
		Map<Class, Integer> result = MapUtil.newMap();

		result.put(String.class, Types.LONGVARCHAR);
		result.put(Integer.class, Types.INTEGER);
		result.put(Long.class, Types.BIGINT);
		result.put(Double.class, Types.DOUBLE);
		result.put(Boolean.class, Types.BOOLEAN);
		result.put(Date.class, Types.DATE);
		result.put(java.util.Date.class, Types.DATE);

		return result;
	}
	private RowSetMetaDataImpl meta = new RowSetMetaDataImpl() ; 
	private MapListRows(Queryable query) throws SQLException {
		super(query);
	}

	public static Rows create(List<Map<String, ? extends Object>> datas, String[] columns) throws SQLException {
		final MapListRows result = new MapListRows(Queryable.Fake);
		result.populate(datas, columns);
		result.beforeFirst();
		return result;
	}

	private RowSetMetaData setMetaData(List<Map<String, ? extends Object>> destList, String[] columns) throws SQLException {
		meta.setColumnCount(columns.length);
		int appendIndex = 0;
		
		Map<String, ? extends Object> firstRow = (destList.size() == 0) ? MapUtil.EMPTY : destList.get(0) ;
		for (int index = 1; index <= columns.length; index++) {
			int myidx = index-1;
			meta.setColumnName(index, columns[myidx]);
			meta.setColumnLabel(index, columns[myidx]);
			meta.setCaseSensitive(index, false) ;
			if (firstRow == MapUtil.EMPTY) {
				meta.setColumnType(index, Types.OTHER) ;
				meta.setColumnTypeName(index, "other") ;
			} else {
				Object value = firstRow.get(columns[myidx]) ;
				meta.setColumnType(index, value == null ? Types.OTHER : TypeMappingMap.get(value.getClass()));
				meta.setColumnTypeName(index, "other") ;
			}
		}
		super.setMetaData(meta) ;
		return meta;
	}

	private void populate(List<Map<String, ? extends Object>> datas, String[] columns) throws SQLException {
		setMetaData(datas, columns);
		// Collections.reverse(nodes) ;

		for (Map<String, ? extends Object> row : datas) {
			appendRow(columns, row);
		}
	}

	private void appendRow(String[] columns, Map<String, ? extends Object> row) throws SQLException {
		super.afterLast();
		super.moveToInsertRow();
		for (int index = 1; index <= columns.length; index++) {
			String colName = getMetaData().getColumnName(index) ;
			super.updateObject(index, row.get(colName));
		}

		super.insertRow();
		super.moveToCurrentRow();
	}
	
	@Override public ResultSetMetaData getMetaData(){
		return meta ;
	}

	private RowSetMetaData makeMetaData(Node node, NodeColumns columns) throws SQLException {
		return columns.getMetaType(ListUtil.create(node));
	}

}
