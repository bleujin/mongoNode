package net.ion.radon.repository;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.RowSetMetaData;

public interface IColumn {
	public Object getValue(Node node) ;
	public int getColumnCount(Node node);
	public int setMeta(Node node, int i, RowSetMetaData meta, Map<Class, Integer> typeMappingMap) throws SQLException;
	public String getLabel() ;
}
