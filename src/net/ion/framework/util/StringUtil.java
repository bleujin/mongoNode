package net.ion.framework.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

public class StringUtil extends org.apache.commons.lang.StringUtils {

	public final static String T = "T";
	public final static String TRUE = "TRUE";

	public static boolean isAlphanumericUnderbar(String str) {

		if (str == null) {
			return false;
		}
		char[] chars = str.toCharArray();
		for (int i = 0, last = chars.length; i < last; i++) {
			char ch = chars[i];
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_') {
				continue;
			} else
				return false;
		}
		return true;
	}

	public static String coalesce(String... strs){
		for (String str : strs) {
			if (isNotBlank(str)) return str ;
		}
		return null ;
	}
	
	public static boolean isSmallAlphaNumUnderBar(String str) {

		if (str == null) {
			return false;
		}
		char[] chars = str.toCharArray();
		for (int i = 0, last = chars.length; i < last; i++) {
			char ch = chars[i];
			if ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_') {
				continue;
			} else
				return false;
		}
		return true;
	}

	public static boolean isIncludeIgnoreCase(String[] array, String arg) {

		if ((array == null) || array.length == 0 || arg == null) {
			return false;
		}

		for (int i = 0, length = array.length; i < length; ++i) {
			if (arg.equalsIgnoreCase(array[i])) {
				return true;
			}
		}
		return false;
	}

	public static String join(int[] ints) {
		return join(ints, ',');
	}

	public static String join(int[] ints, char c) {
		String[] s = new String[ints.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = String.valueOf(ints[i]);
		}

		return StringUtils.join(s, c);
	}

	public static String[] splitWorker(String str, String separator) {

		if (separator == null) {
			return split(str);
		} else if (separator.length() == 1) {
			return split(str, separator);
		} else {
			List<String> list = new ArrayList<String>();

			int idx = 0;
			while (true) {
				idx = str.indexOf(separator);
				if (idx == -1) {
					list.add(str);
					break;
				}
				String split = str.substring(0, idx);
				if (StringUtils.isNotEmpty(split)) {
					list.add(split);
				}
				str = str.substring(idx + separator.length());
			}
			return list.toArray(new String[0]);
		}
	}

	public static String escapeControlChar(String str) {
		char[] charArray = str.toCharArray();
		char[] target = new char[charArray.length];

		int k = 0;
		for (int i = 0; i < charArray.length; i++) {
			// if (charArray[i] >= 32 || charArray[i] == '\n' || charArray[i] ==
			// '\r' || charArray[i] == '\t') {
			if (Character.isIdentifierIgnorable(charArray[i]))
				continue;
			if (charArray[i] >= 32 || charArray[i] == '\n' || charArray[i] == '\r' || charArray[i] == '\t') {
				target[k++] = charArray[i];
			} else {
			}
		}

		char[] result = new char[k];
		System.arraycopy(target, 0, result, 0, k);

		return new String(result);
	}

	public static String escapeControlChar2(String str) {
		char charArray[] = str.toCharArray();

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] >= 32 || charArray[i] == '\n' || charArray[i] == '\r' || charArray[i] == '\t') {
				buffer.append(charArray[i]);
			}
		}

		return buffer.toString();
	}

	public static Vector<Object> listToVector(List<Object> list) {
		Vector<Object> vector = new Vector<Object>();
		vector.addAll(list);
		return vector;

	}

	public static Vector<Object> arrayToVector(Object[] obj) {
		Vector<Object> vector = new Vector<Object>();
		for (int i = 0, last = obj.length; i < last; i++) {
			vector.add(obj[i]);
		}

		return vector;
	}

	public final static boolean toBoolean(String str) {
		if (StringUtil.isBlank(str))
			return false;
		return "T".equalsIgnoreCase(str) || "TRUE".equalsIgnoreCase(str);
	}

	public static int[] stringToArrayInt(String arrIntVal) {
		return stringToArrayInt(arrIntVal, ",");
	}

	public static int[] stringToArrayInt(String arrIntVal, String div) {

		String[] strArtIds = StringUtils.split(arrIntVal, div);
		int[] artIds = new int[strArtIds.length];
		for (int i = 0; i < strArtIds.length; i++) {
			artIds[i] = Integer.parseInt(strArtIds[i]);
		}
		return artIds;
	}

	private static final long byteSize = 1024;

	public static String toFileSize(long size) {
		NumberFormat nf = NumberFormat.getInstance();
		long result = 0;
		if (size < byteSize) {
			result = (size != 0) ? 1 : 0;
		} else {
			long modSize = size % byteSize;
			if (modSize != 0) {
				result = (size / byteSize) + 1;

			} else {
				result = (size / byteSize);
			}
		}

		return nf.format(result) + " KB";
	}

	public static String filterHTML(String value) {
		if (value == null) {
			return (null);
		}

		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuilder result = new StringBuilder(content.length + 50);
		for (int i = 0; i < content.length; i++) {
			switch (content[i]) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&#39;");
				break;
			case ' ':
				result.append("&nbsp;");
				break;
			default:
				result.append(content[i]);
			}
		}
		return (result.toString());
	}

	public static String toString(Object value) {
		return (value == null) ? null : value.toString();
	}

	public static boolean isBetween(String value, String start, String end) {
		return startsWith(value, start) && endsWith(value, end);
	}

	public static String toString(Object value, String defaultString) {
		return (value == null) ? defaultString : value.toString();
	}

}
