package net.ion.radon.repository;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.RowSetMetaData;

import net.ion.framework.db.rowset.RowSetMetaDataImpl;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

public class NodeColumns {
	
	private List<IColumn> columnList = ListUtil.newList() ;
	private String screenColName ; 
	
	
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
	
	public NormalColumn[] getColumns() {
		return columnList.toArray(new NormalColumn[0]);
	}

	private NodeColumns() {
	}

	public static NodeColumns create(String... columns) throws SQLException{
		if(columns == null || columns.length == 0){
			throw new SQLException("columns not exist");
		}
		
		NodeColumns result = new NodeColumns();
		for (int i = 0; i < columns.length; i++) {
			result.columnList.add(Column.parse(columns[i])) ;
		}
		return result ;
	}

	public static NodeColumns create(IColumn... columns) throws SQLException{
		if(columns == null || columns.length == 0){
			throw new SQLException("columns not exist");
		}
		
		NodeColumns result = new NodeColumns();
		for (IColumn col : columns) {
			result.columnList.add(col) ;
		}
		return result ;
	}
	

	
	public int size() {
		return columnList.size();
	}

	public IColumn get(int columnIndex) {
		return columnList.get(columnIndex-1);
	}

	public void setScreenColName(String screenColName){
		this.screenColName = screenColName ;
		columnList.add(Column.parse(screenColName)) ;
	}
	
	public String getScreenColName(){
		return this.screenColName ;
	}

	public boolean contains(String key) {
		for (IColumn col : columnList) {
			if (key.equalsIgnoreCase(col.getLabel())) {
				return true ;
			}
		}
		return false ;
	}
	
	private int getColumnSize(Node node) {
		int sumSize = 0 ;
		for (int i = 1; i <= size(); i++) {
			sumSize += columnList.get(i-1).getColumnCount(node) ;
		}
		return sumSize;
	}

	public RowSetMetaData getMetaType(List<Node> destList) throws SQLException {
		if (destList.size() > 0){
			return getMetaType(destList.get(0)) ;
		} else {
			return defaultMetaType() ;
		}
	}

	private RowSetMetaData getMetaType(Node node) throws SQLException {
		RowSetMetaData meta = new RowSetMetaDataImpl();
		
		int sumSize = getColumnSize(node);
		
		meta.setColumnCount(sumSize);
		int appendIndex = 0 ;
		for (int i = 1; i <= size(); i++) {
			IColumn column = columnList.get(i-1) ;
			appendIndex += column.setMeta(node, appendIndex + i, meta, TypeMappingMap) ;
		}
		
		return meta;
	}
	
	private RowSetMetaData defaultMetaType() throws SQLException {
		RowSetMetaData meta = new RowSetMetaDataImpl();
		meta.setColumnCount(size());
		
		for (int i = 1; i <= size(); i++) {
			IColumn column = columnList.get(i-1) ;

			meta.setColumnName(i, column.getLabel());
			meta.setColumnLabel(i, column.getLabel());
			meta.setColumnType(i, Types.OTHER);
		}
		
		return meta;
	}

}


