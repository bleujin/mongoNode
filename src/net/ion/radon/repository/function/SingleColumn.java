package net.ion.radon.repository.function;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import javax.sql.RowSetMetaData;

import net.ion.radon.repository.IColumn;
import net.ion.radon.repository.Node;

public abstract class SingleColumn implements IColumn {

	public int setMeta(Node node, int index, RowSetMetaData meta, Map<Class, Integer> mapping) throws SQLException {

		meta.setColumnName(index, getLabel());
		meta.setColumnLabel(index, getLabel());
		Object value = getValue(node);
		meta.setColumnType(index, value == null ? Types.OTHER : mapping.get(value.getClass()));

		return 0;
	}

	public int getColumnCount(Node node) {
		return 1;
	}

}