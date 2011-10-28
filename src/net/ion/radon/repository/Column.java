package net.ion.radon.repository;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.RowSetMetaData;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.function.NvlFunction;
import net.ion.radon.repository.function.SingleColumn;

public class Column {
	public static IColumn nvl(String... cols) {
		List<String> list = ListUtil.toList(cols) ;
		return new NvlFunction((String[])(list.subList(0, list.size()-1).toArray(new String[0])), cols[cols.length-1]);
	}

	public static IColumn constant(Object con, String label) {
		return new ConstantColumn(con, label);
	}

	private static String[] getParams(String expression){
		List<String> list = ListUtil.newList();
		String[] params = StringUtil.split(expression, ",");

		String value = "";
		for(int i=0; i< params.length; i++){
			if(StringUtil.isEmpty(value)) 
				value = params[i];
			else{
				value = value + "," + params[i];
			}
			if( isIncludeCountMatches(value, "(", ")")){
				list.add(value);
				value = "";
			}
		}
		return list.toArray(new String[0]);
	}
	
	private static boolean isIncludeCountMatches(String value, String startKey, String endKey) {
		return StringUtil.countMatches(value, startKey) == StringUtil.countMatches(value, endKey);
	}
	
	public static IColumn parse(String expression) {
		// decode(a, b) c
		// decode(r.a, r.b) c
		// decode(r.a, 'constant') c
		// String expression = _expression.toLowerCase();
		try {
			if (isFunctionExpression(expression)) {
				String fnName = StringUtil.lowerCase(StringUtil.substringBefore(expression, "("));
				String paramString = StringUtil.substringAfter(expression, "(");
				
				paramString = StringUtil.substringBeforeLast(paramString, ")");
				String[] params = getParams(paramString);
//				String[] params = StringUtil.substringBeforeLast(paramString, ")").split(",");
//				for (int i = 0; i < params.length; i++) {
//					params[i] = params[i].trim() ;
//				}
				
				String alias = StringUtil.defaultIfEmpty(StringUtil.substringAfterLast(expression, ")").trim(), expression);
				Class clz = Class.forName("net.ion.radon.repository.function." + StringUtil.capitalize(fnName) + "Function");
	            Object[] passed = {params, alias};
				IColumn col = (IColumn) clz.getConstructor(String[].class, String.class).newInstance(passed);
				return col;
			} else if (isConstantExpression(expression)) {
				String[] cols = StringUtil.split(expression, " ") ; // @TODO : not sufficiency
				Object value = NumberUtil.isNumber(cols[0]) ? Integer.parseInt(cols[0]) : StringUtil.substringBetween(cols[0], "'", "'") ;
				String alias = (cols.length == 1 ? ObjectUtil.toString(value) : cols[1]) ; 
				
				return new ConstantColumn(value, alias);
			} else {
				String[] exps = StringUtil.split(expression, ". ");
				if (exps.length == 1) {
					return new NormalColumn(exps[0], exps[0]);
				} else if (exps.length == 2 && expression.contains(".")) { // a.b
					return new ReferenceColumn(expression, exps[1]);
				} else if (exps.length == 2 && expression.contains(" ")) { // a b
					return new NormalColumn(exps[0], exps[1]);
				} else if (exps.length == 3 && expression.contains(".")) { // a.b c
					return new ReferenceColumn(exps[0] + "." + exps[1], exps[2]);
				} else {
					throw new IllegalArgumentException(expression + " is illegal expression");
				}
			}
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static boolean isConstantExpression(String expression) {
		return expression.startsWith("'") || NumberUtil.isNumber(expression);
	}

	private static List<String> FunctionName = ListUtil.toList("nvl", "tochar", "decode", "substr", "sign", "length", "power", "divide", 
			  "floor", "mod", "tonumber", "append", "lpad", "minus", "min", "max");

	private static boolean isFunctionExpression(String expression) {
		String fnName = StringUtil.lowerCase(StringUtil.substringBefore(expression, "("));
		return FunctionName.contains(fnName);
	}
}

class ReferenceColumn extends SingleColumn {

	private static HashMap<String, String> relTypeMap = new HashMap<String, String>();
	static {
		relTypeMap.put("r", ":reference:") ;
		relTypeMap.put("w", ":workspace:") ;
		relTypeMap.put("a", ":aradon:") ;
	}
	
	private String relName;
	private String colName;
	private String label;
	private String relType;

	ReferenceColumn(String targetColumn, String label) {
		//a.b
		this.relType = (targetColumn.contains(":"))?  relTypeMap.get(StringUtil.substringBefore(targetColumn, ":")) : ":aradon:";
		this.relName = (targetColumn.contains(":"))?  StringUtil.substringBetween(targetColumn, ":", ".") : StringUtil.substringBefore(targetColumn, ".");
		this.colName = StringUtil.substringAfterLast(targetColumn, ".");
		this.label = label;
	}

	public Object getValue(Node node) {
		
		ReferenceTaragetCursor rcursor = node.getSession().createRefQuery().from(node, relType + StringUtil.lowerCase(relName)).find();
		if (rcursor.hasNext()) {
			return rcursor.next().get(colName);
		}
		return null;
	}

	public String getTargetGroup() {
		return relName;
	}

	public String getLabel() {
		return label;
	}

}

class NormalColumn extends SingleColumn {

	private String targetColumn;
	private String label;

	NormalColumn(String targetColumn, String label) {
		this.targetColumn = targetColumn.toLowerCase();
		this.label = label.toLowerCase();
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label + " ( " + targetColumn + " ) ";
	}

	public Object getValue(Node node) {
		return node.get(targetColumn);
	}

}

class ConstantColumn extends SingleColumn {

	private Object con;
	private String label;

	ConstantColumn(Object con, String label) {
		this.con = con;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Object getValue(Node node) {
		return con;
	}

}







@Deprecated
class AstericColumn implements IColumn {

	AstericColumn(String expression) {
	}

	public String getLabel() {
		return null;
	}

	public Object getValue(Node node) {
		return null;
	}

	public int getColumnCount(Node node) {
		return node.toPropertyMap().size();
	}

	public int setMeta(Node node, int index, RowSetMetaData meta, Map<Class, Integer> typeMappingMap) throws SQLException {

		// TODO Auto-generated method stub
		return getColumnCount(node) - 1;
	}
}
