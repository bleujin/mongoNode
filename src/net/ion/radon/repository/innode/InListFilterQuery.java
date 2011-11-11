package net.ion.radon.repository.innode;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

public class InListFilterQuery {

	private final String field;
	private final SessionQuery squery;
	private final String filterFn;

	
	private StringBuilder withPropertyExp = new StringBuilder() ;
	private PropertyFamily options = PropertyFamily.create().put("fromindex", 0).put("toindex", 1000000) ;
	private InListFilterQuery(String field, SessionQuery squery, String filterFn) {
		this.field = field ;
		this.squery = squery ;
		this.filterFn = filterFn ;
	}

	public static InListFilterQuery create(String field, SessionQuery squery, Session session, String filterFn) {
		return new InListFilterQuery(field, squery, filterFn);
	}

	public InListFilterQuery withProperty(String... cols) {
		for (String col : cols) {
			withPropertyExp.append(", " + col + ":this." + col) ;
		}
		return this;
	}

	public InListFilterQuery sort(String sortKey, boolean asc) {
		options.put("sortkey", sortKey).put("order", asc ? 1 : -1) ;
		return this;
	}

	public InListFilterQuery inlistPage(PageBean page) {
		options.put("fromindex", page.getStartLoc()).put("toindex", page.getStartLoc() + page.getListNum()) ;
		return this;
	}

	public NodeCursor find() {
		Object[] args = {options.toJSONString(), filterFn, field, withPropertyExp.toString()} ;
		
		String params = "var _params = %1$s; \n";
		params += "_params.comfn = function(f1, f2) { " + "if (f1[_params.sortkey] > f2[_params.sortkey]) return 1 * _params.order; " + "else if (f1[_params.sortkey] < f2[_params.sortkey]) return -1 * _params.order; " + "else return 0; " + "}; \n";
		params += "_params.filter = %2$s; \n";
		params += "\n" +
				" Array.prototype.filter = function(fun, thisp) {\n" +
				" 	var len = this.length; \n" + 
				"   if (typeof fun != 'function') throw new TypeError(); \n" + 
				"   var res = []; \n" + 
				"   for (var i = 0; i < len; i++){ \n" + 
				"      if (i in this){ \n" + 
				"        var val = this[i];  \n" + 
				"        if (fun.call(thisp, val, i, this)) res.push(val); \n" + 
				"      } \n" + 
				"   } \n" + 
				"   return res; \n" + 
				" };\n" ;
		String mapFn = "function(){ \n" +
				params + "\n" + 
				" var myresult = Array.prototype.filter.call(this.%3$s, _params.filter, this.%3$s) ; \n" + 
				(options.getDBObject().containsField("sortkey") ? " Array.prototype.sort.call(myresult, _params.comfn); \n"  :"")+
				" myresult = Array.prototype.slice.call(myresult, _params.fromindex, _params.toindex); \n" + 
				" emit(this._id, {%3$s:myresult %4$s});}\n";
		
		String mapFunction = String.format(mapFn, args) ;
//		Debug.line(mapFunction) ;
		return squery.mapreduce(mapFunction, "", "") ;
	}


}
