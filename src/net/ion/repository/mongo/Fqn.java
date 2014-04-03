package net.ion.repository.mongo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.ion.framework.parse.gson.JsonPrimitive;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.util.uriparser.URIPattern;
import net.ion.radon.util.uriparser.URIResolveResult;
import net.ion.radon.util.uriparser.URIResolver;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Fqn implements Comparable<Fqn>, Serializable {

	private static final long serialVersionUID = -2662673757638992543L;
	public static final String SEPARATOR = "/";

	private final String[] elements;
	private transient int hash_code = 0;

	public static final Fqn ROOT = new Fqn();
	public static final Fqn TRANSACTIONS = Fqn.fromString("/__transactions");

	protected String stringRepresentation;
	private static final String[] EMPTY_ARRAY = new String[0];

	private Fqn(String... elements) {
		this.elements = elements;
	}

	private Fqn(List<String> names) {
		elements = (names != null) ? names.toArray(new String[0]) : EMPTY_ARRAY;
	}

	private Fqn(Fqn base, Object... relative) {
		elements = new String[base.elements.length + relative.length];
		System.arraycopy(base.elements, 0, elements, 0, base.elements.length);
		System.arraycopy(relative, 0, elements, base.elements.length, relative.length);
	}

	@SuppressWarnings("unchecked")
	public static Fqn fromList(List<String> names) {
		return new Fqn(names);
	}

	public static Fqn fromElements(String... elements) {
		String[] copy = new String[elements.length];
		System.arraycopy(elements, 0, copy, 0, elements.length);
		return new Fqn(copy);
	}

	public static Fqn fromRelativeFqn(Fqn base, Fqn relative) {
		return new Fqn(base, relative.elements);
	}

	public static Fqn fromRelativeList(Fqn base, List<?> relativeElements) {
		return new Fqn(base, relativeElements.toArray());
	}

	public static Fqn fromRelativeElements(Fqn base, Object... relativeElements) {
		return new Fqn(base, relativeElements);
	}

	public static Fqn fromString(String stringRepresentation) {
		if (stringRepresentation == null || stringRepresentation.equals(SEPARATOR) || stringRepresentation.length() == 0)
			return root();

		String toMatch = stringRepresentation.startsWith(SEPARATOR) ? stringRepresentation.substring(1) : stringRepresentation;
		String[] el = toMatch.split(SEPARATOR);
		// return new Fqn(el) ;
		return new Fqn(Iterables.toArray(Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(toMatch), String.class));
	}

	public Fqn relativeFqn(String childFqn){
		return Fqn.fromRelativeFqn(this, Fqn.fromString(childFqn)) ;
	}
	
	public Fqn getAncestor(int generation) {
		if (generation == 0)
			return root();
		return getSubFqn(0, generation);
	}

	public Fqn getSubFqn(int startIndex, int endIndex) {
		if (endIndex < startIndex)
			throw new IllegalArgumentException("End index cannot be less than the start index!");
		int len = endIndex - startIndex;
		String[] el = new String[len];
		System.arraycopy(elements, startIndex, el, 0, len);
		return new Fqn(el);
	}

	public int size() {
		return elements.length;
	}

	public Object get(int n) {
		return elements[n];
	}

	public JsonPrimitive toJson() {
		return new JsonPrimitive(toString());
	}

	public Object getLastElement() {
		if (isRoot())
			return null;
		return elements[elements.length - 1];
	}

	public boolean hasElement(Object element) {
		return indexOf(element) != -1;
	}

	private int indexOf(Object element) {
		if (element == null) {
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] == null)
					return i;
			}
		} else {
			for (int i = 0; i < elements.length; i++) {
				if (element.equals(elements[i]))
					return i;
			}
		}
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Fqn)) {
			return false;
		}
		Fqn other = (Fqn) obj;
		if (elements.length != other.elements.length)
			return false;
		for (int i = elements.length - 1; i >= 0; i--) {
			if (!safeEquals(elements[i], other.elements[i]))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (hash_code == 0) {
			hash_code = calculateHashCode();
		}
		return hash_code;
	}

	@Override
	public String toString() {
		if (stringRepresentation == null) {
			stringRepresentation = getStringRepresentation(elements);
		}
		return stringRepresentation;
	}

	public boolean isChildOf(Fqn parentFqn) {
		return parentFqn.elements.length != elements.length && isChildOrEquals(parentFqn);
	}

	public boolean isDirectChildOf(Fqn parentFqn) {
		return elements.length == parentFqn.elements.length + 1 && isChildOf(parentFqn);
	}

	public boolean isChildOrEquals(Fqn parentFqn) {
		Object[] parentEl = parentFqn.elements;
		if (parentEl.length > elements.length) {
			return false;
		}
		for (int i = parentEl.length - 1; i >= 0; i--) {
			if (!safeEquals(parentEl[i], elements[i]))
				return false;
		}
		return true;
	}

	protected int calculateHashCode() {
		int hashCode = 19;
		for (Object o : elements)
			hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
		if (hashCode == 0)
			hashCode = 0xDEADBEEF; // degenerate case
		return hashCode;
	}

	protected String getStringRepresentation(Object[] elements) {
		StringBuilder builder = new StringBuilder();
		for (Object e : elements) {
			// incase user element 'e' does not implement equals() properly, don't rely on their implementation.
			if (!SEPARATOR.equals(e) && !"".equals(e)) {
				builder.append(SEPARATOR);
				builder.append(e);
			}
		}
		return builder.length() == 0 ? SEPARATOR : builder.toString();
	}

	public Fqn getParent() {
		switch (elements.length) {
		case 0:
		case 1:
			return root();
		default:
			return getSubFqn(0, elements.length - 1);
		}
	}

	public static Fqn root() { // declared final so compilers can optimise and in-line.
		return ROOT;
	}

	public boolean isRoot() {
		return elements.length == 0;
	}

	public String getLastElementAsString() {
		if (isRoot()) {
			return SEPARATOR;
		} else {
			Object last = getLastElement();
			if (last instanceof String)
				return (String) last;
			else
				return String.valueOf(getLastElement());
		}
	}

	public List<String> peekElements() {
		return Arrays.asList(elements);
	}

	@Override
	public int compareTo(Fqn fqn) {
		return FqnComparator.INSTANCE.compare(this, fqn);
	}

	public Fqn replaceAncestor(Fqn oldAncestor, Fqn newAncestor) {
		if (!isChildOf(oldAncestor))
			throw new IllegalArgumentException("Old ancestor must be an ancestor of the current Fqn!");
		Fqn subFqn = this.getSubFqn(oldAncestor.size(), size());
		return Fqn.fromRelativeFqn(newAncestor, subFqn);
	}

	public String name() {
		return ObjectUtil.toString(getLastElement());
	}

	public String startWith() {
		return isRoot() ? "/*" : toString() + "/*";
	}

	private static boolean safeEquals(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}

	public BasicDBObject idQueryObject() {
		BasicDBObject result = new BasicDBObject("_id", toString()) ;
		result.put("_parent", getParent().toString()) ;
		return result ;
	}

	public DBObject parentQueryObject() {
		return new BasicDBObject("_parent", getParent().toString()) ;
	}

	public DBObject childrenQueryObject() {
		return new BasicDBObject("_id", new BasicDBObject("$gt", (getParent().isRoot() ? "" : getParent().toString()) + "/")) ;
	}

	public static Fqn fromDBObject(DBObject dbo) {
		return Fqn.fromString(dbo.get("_id").toString());
	}

	public int depth() {
		return elements.length;
	}


	public boolean isPattern(String fqnPattern) {
		return new URIPattern(fqnPattern).match(this.toString());
	}
	
	public Map<String, String> resolve(String fqnPattern){
		URIResolveResult resolver = new URIResolver(toString()).resolve(new URIPattern(fqnPattern));
		Map<String, String> result = MapUtil.newMap() ;
		
		for(String name : resolver.names()){
			result.put(name, ObjectUtil.toString(resolver.get(name))) ;
		}
		
		return result ;
	}
	
}

class FqnComparator implements Comparator<Fqn>, Serializable {
	public static final FqnComparator INSTANCE = new FqnComparator();
	private static final long serialVersionUID = -1357631755443829281L;

	/**
	 * Returns -1 if the first comes before; 0 if they are the same; 1 if the second Fqn comes before. <code>null</code> always comes first.
	 */
	@Override
	public int compare(Fqn fqn1, Fqn fqn2) {
		int s1 = fqn1.size();
		int s2 = fqn2.size();

		if (s1 == 0) {
			return (s2 == 0) ? 0 : -1;
		}

		if (s2 == 0) {
			return 1;
		}

		int size = Math.min(s1, s2);

		for (int i = 0; i < size; i++) {
			Object e1 = fqn1.get(i);
			Object e2 = fqn2.get(i);
			if (e1 == e2) {
				continue;
			}
			if (e1 == null) {
				return 0;
			}
			if (e2 == null) {
				return 1;
			}
			if (!e1.equals(e2)) {
				int c = compareElements(e1, e2);
				if (c != 0) {
					return c;
				}
			}
		}

		return s1 - s2;
	}

	/**
	 * Compares two Fqn elements. If e1 and e2 are the same class and e1 implements Comparable, returns e1.compareTo(e2). Otherwise, returns e1.toString().compareTo(e2.toString()).
	 */
	@SuppressWarnings("unchecked")
	private int compareElements(Object e1, Object e2) {
		if (e1.getClass() == e2.getClass() && e1 instanceof Comparable) {
			return ((Comparable<Object>) e1).compareTo(e2);
		} else {
			return e1.toString().compareTo(e2.toString());
		}
	}

}
