	package org.cehl.commons;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class StringUtils {
	/**
	 * Comma list.
	 *
	 * @param values the values
	 * @return the string
	 */
	public static String commaList(Object[] values) {
		 return commaList(values, "");
	}
	
	/**
	 * Comma list.
	 *
	 * @param values the values
	 * @param delimiter the delimiter
	 * @return the string
	 */
	public static String commaList(Object[] values, String delimiter) {
		 return commaList(Arrays.asList(values), delimiter);
	}
	

	/**
	 * Comma list.
	 * 
	 * @param values
	 *            the values
	 * @return the string
	 */
	public static String commaList(Collection<?> values) {
		return commaList(values,"");
	}

	/**
	 * Comma list.
	 * 
	 * @param values
	 *            the values
	 * @param delimiter
	 *            the delimiter
	 * @return the string
	 */
	public static String commaList(Collection<?> values, String delimiter) {
		return commaList(values.iterator(), delimiter);
	}	
	
	/**
	 * Comma list.
	 *
	 * @param itList the it list
	 * @return the string
	 */
	public static String commaList(Iterator<?> itList) {
		return commaList(itList,"");
	}
	
	/**
	 * Comma list.
	 *
	 * @param itList the it list
	 * @param delimiter the delimiter
	 * @return the string
	 */
	public static String commaList(Iterator<?> itList, String delimiter) {
		  String list = "";
		  
		  while(itList.hasNext()) {
			  list += (delimiter + itList.next() + delimiter);
			  list += itList.hasNext() ? "," : ""; 
		  }

		  return list;
	}

	public static boolean isEmpty(String string) {
		if(string != null) {
			string = string.trim();
		}
		
		return org.apache.commons.lang3.StringUtils.isEmpty(string);
	}

}
