package net.ion.radon.repository;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.RowSetMetaData;

import net.ion.framework.db.Rows;
import net.ion.framework.db.RowsImpl;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.ListUtil;
import net.ion.radon.repository.myapi.ICursor;


public class NodeRows extends RowsImpl {
	
	
	private NodeRows(Queryable query) throws SQLException {
		super(query);
	}
	
	private static NodeRows makeBlankRows(Queryable query, NodeColumns columns) throws SQLException {
		NodeRows rows = new NodeRows(query);
		rows.setMetaData(ListUtil.EMPTY, columns);
		return rows;
	}

	
	public static NodeRows createByNode(Queryable query, Node node, NodeColumns columns) throws SQLException {
	if (node == null){
			return makeBlankRows(query, columns);
		}

		final NodeRows result = new NodeRows(query);
		result.populate(node, columns);
		result.beforeFirst();
		return result;
	}
	

	public static NodeRows createByScreen(Queryable query, NodeScreen nodeScreen, NodeColumns columns) throws SQLException {
		final NodeRows result = new NodeRows(query);
		result.populate(nodeScreen, columns);
		result.beforeFirst();
		return result;
	}

	public static Rows createByCursor(Queryable query, ICursor cursor, NodeColumns columns) throws SQLException {
		final NodeRows result = new NodeRows(query);
		result.populate(cursor, columns);
		result.beforeFirst();
		return result;
	}
	
	public static Rows createByList(Queryable query, List<Node> nodes, NodeColumns columns) throws SQLException {
		final NodeRows result = new NodeRows(query);
		result.populate(nodes, columns);
		result.beforeFirst();
		return result;
	}

	
	public static NodeRows createByNode(Queryable query, Node node, String... columns) throws SQLException {
		return createByNode(query, node, NodeColumns.create(columns));
	}
	public static NodeRows createByScreen(Queryable query, NodeScreen nodeScreen, String... columns) throws SQLException {
		return createByScreen(query, nodeScreen, NodeColumns.create(columns));
	}

	public static Rows createByCursor(Queryable query, ICursor cursor, String... columns) throws SQLException {
		return createByCursor(query, cursor, NodeColumns.create(columns));
	}
	
	public static Rows createByList(Queryable query, List<Node> nodes, String... columns) throws SQLException {
		return createByList(query, nodes, NodeColumns.create(columns));
	}
	

	public static Rows unionAll(Rows... rowss) throws SQLException {
		final NodeRows result = new NodeRows(Queryable.Fake);
		result.setMetaData((RowSetMetaData)rowss[0].getMetaData()) ;
		
		ResultSetMetaData meta = result.getMetaData() ;
		for (Rows rows : rowss) {
			rows.beforeFirst() ;;
			while(rows.next()){
				result.afterLast() ;
				result.moveToInsertRow();
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					result.updateObject(i, rows.getObject(i)) ;
				}
				result.insertRow();
				result.moveToCurrentRow();
			}
		}
		return result ;
	}

	
	
	public static Rows addCursor(NodeRows result , ICursor cursor, NodeColumns columns) throws SQLException {
		result.populate(cursor, columns);
		return result;
	}
	
	public Rows addCursor(ICursor cursor, NodeColumns columns) throws SQLException{
		populate(cursor, columns);
		return this;
	}

	private void setMetaData(List<Node> destList, NodeColumns columns) throws SQLException {
		setMetaData(columns.getMetaType(destList)) ;
	}
	
	
	private void populate(List<Node> nodes, NodeColumns columns) throws SQLException {
		setMetaData(nodes, columns);
		// Collections.reverse(nodes) ;
		
		for(Node node : nodes) {
			appendRow(columns, node, 0);
		}
	}
	
	private void populate(NodeScreen nodeScreen, NodeColumns columns) throws SQLException {
		List<Node> sourceList = nodeScreen.getPageNode();
//		List<Node> destList = new ArrayList<Node>(sourceList) ;
//		Collections.reverse(destList) ;
//		
		setMetaData(sourceList, columns) ;
		for(Node node : sourceList) {
			appendRow(columns, node, nodeScreen.getScreenSize());
		}
	}
	
	private void populate(ICursor cursor, NodeColumns columns) throws SQLException {
		if (cursor.hasNext()) {
			Node firstRow = cursor.next() ;
			RowSetMetaData meta = makeMetaData(firstRow, columns);
			setMetaData(meta);
			appendRow(columns, firstRow, 0);
			while(cursor.hasNext()){
				appendRow(columns, cursor.next(), 0) ;
			}
		}else{
			setMetaData(ListUtil.EMPTY, columns) ;
		}
	}

	private void populate(Node node, NodeColumns columns) throws SQLException {
		RowSetMetaData meta = makeMetaData(node, columns);
		setMetaData(meta);

		appendRow(columns, node, 0);
	}

	private void appendRow(NodeColumns columns, Node firstRow, int screenSize) throws SQLException {
		super.afterLast() ;
		super.moveToInsertRow();
		for (int i = 1; i <= columns.size(); i++) {
			final IColumn column = columns.get(i);
			if (column.getLabel().equals(columns.getScreenColName())){
				super.updateObject(i, screenSize) ;
			} else {
				// column.setValue(i, this) ;
				
				super.updateObject(i,  column.getValue(firstRow));
			}
		}
		
		super.insertRow();
		super.moveToCurrentRow();
	}

	private RowSetMetaData makeMetaData(Node node, NodeColumns columns) throws SQLException {
		return columns.getMetaType(ListUtil.create(node)) ;
	}



}
