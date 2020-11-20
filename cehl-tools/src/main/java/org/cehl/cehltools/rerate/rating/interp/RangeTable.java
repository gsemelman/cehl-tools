package org.cehl.cehltools.rerate.rating.interp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.rating.interp.AbstractInterpolator.BOUNDARY_METHOD;
import org.cehl.cehltools.rerate.rating.interp.AbstractInterpolator.INTERPOLATION_TYPE;
import org.cehl.cehltools.rerate.rating.interp.InterpolatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RangeTable implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(RangeTable.class);
	
	//key typing enables the interface to work with strings rather than specific types
	//the string key is converted to a specific type internally
	//the typed key must implement Comparable
	public enum KEYTYPE {NUMERIC,STRING};
	
	private Map rowMap; 	
	private String name;
	
	private RangeTableValueFormatter rangeTableValueFormatter= null;
	
	public RangeTable() {
		rowMap = new TreeMap();

	}
		
	public void insertValue(Number key, Object value) {
		rowMap.put(getTypedKey(key),value);
	}
	
	public boolean containsKey(Number key) {
		return rowMap.containsKey(getTypedKey(key));
	}
	
	
	
	public List<Comparable[]> getRangeList() {
		
		List<Comparable[]> rangeList = new ArrayList<Comparable[]>();
		
		int rowIndex = 0;
		for(Map.Entry entrySet : (Set<Map.Entry>)rowMap.entrySet()) {
			
			Comparable[] range = new Comparable[2];
			range[1] = (Comparable)entrySet.getKey();
			
			rangeList.add(range);
			
			rowIndex++;
			
		}

		for(int i=0; i < rangeList.size(); i++) {
			Comparable[] range = (Comparable[])rangeList.get(i);
			if(i == 0) {
				range[0] = new BigDecimal(0);
			}
			else {
				Comparable[] rangePrev = rangeList.get(i-1);
				range[0] = rangePrev[1];
			}
		}

		return rangeList;
		
	}
	
	public Object getValueAt(int row) {
		return rowMap.values().toArray()[row];
	}
		
	public Object getKeyAt(int row) {
		return rowMap.keySet().toArray()[row];
	}
	
	public void setValueAt(int row, Object value) {
		rowMap.put(getKeyAt(row), value);
	}
	
	public String[] getRangeAt(int row) {

		String rangeStart = null;
		String rangeEnd = null;
		String range = null;
		
		//this needs to include the infinity value, if it is defined
		Object[] keySetArray = rowMap.keySet().toArray();
		
		if(row == 0) {
			//first row
			rangeStart = "0";
			rangeEnd = keySetArray[0].toString();
		}
		else if(row < rowMap.size()) {
			//middle row
			rangeStart = keySetArray[row-1].toString();
			rangeEnd = keySetArray[row].toString();
		}
		
		return new String[]{rangeStart,rangeEnd};
		
	}
	
	public int getRangeRowCount() {
		return rowMap.size();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private String formatRangeTableValue(Object value) {
		
		return rangeTableValueFormatter == null ? 
				value.toString() : rangeTableValueFormatter.formatValue(value);
		
	}
	
	@Override
	public String toString() {
	
		String ret = "\nPricing table: ";

		List<Comparable[]> rangeList = getRangeList();
		
		int index = 0;
		Comparable rangeEnd=null;
		for(Map.Entry entrySet : (Set<Map.Entry>)rowMap.entrySet()) {
							
			ret += "\n  range[> " + rangeList.get(index)[0] + ", <= " 
				+ rangeList.get(index)[1] + "]"
				+ " value[" 
				+ formatRangeTableValue(entrySet.getValue()) 
				+ "]";
			
			rangeEnd = rangeList.get(index)[1];
			
			index++;
		}
				
		return ret;
		
	}

	

	private Comparable getTypedKey(Object key) {

		return new BigDecimal(key + "");
		
	}

	public void removeValue(Object value) {
		rowMap.remove(getTypedKey(value));
	}
	
	public void removeValueAt(int row) {
		removeValue(getKeyAt(row));
	}

	public RangeTableValueFormatter getRangeTableValueFormatter() {
		return rangeTableValueFormatter;
	}

	public void setRangeTableValueFormatter(
			RangeTableValueFormatter rangeTableValueFormatter) {
		this.rangeTableValueFormatter = rangeTableValueFormatter;
	}
	
	public void setValue(String key,Object value) {
		if(rowMap.containsKey(key)) {
			rowMap.put(key,value);
		}
	}
	
	public List<String> keyList() {
		
		List keyList = new ArrayList<String>();
		
		Object[] keys = rowMap.keySet().toArray();
		for(int i=0; i < keys.length; i++) {
			keyList.add(keys[i].toString());
		}
		
		return keyList;
		
	}
	
	public boolean isEmpty(){
		return MapUtils.isEmpty(rowMap);
	}
	
	public double[] getKeyValues() {

		RangeTable rangeTable = this;
		double[] keyValues = new double[rangeTable.getRangeRowCount()];
		for(int i=0; i < rangeTable.getRangeRowCount(); i++) {
			keyValues[i] = ((BigDecimal)rangeTable.getKeyAt(i)).doubleValue();
		}
		
		return keyValues;
		
	}
	
	public String[] getExpressions() {

		RangeTable rangeTable = this;
		String[] expressions = new String[rangeTable.getRangeRowCount()];
		for(int i=0; i < rangeTable.getRangeRowCount(); i++) {
			//expressions[i] = (String)rangeTable.getValueAt(i);
			expressions[i] = Objects.toString(rangeTable.getValueAt(i));
			
			
		}

		return expressions;
		
	}
	
	public double[] getExpressionValues() {
	
		String[] expressions = getExpressions();

		double[] expressionValues = new double[expressions.length];
		for(int i=0; i < expressions.length; i++) {
			
			validateExpression(expressions[i]);
			
			//TODO: rounding???
			BigDecimal expressionValue = new BigDecimal(expressions[i]);
			
			expressionValues[i] = expressionValue.doubleValue();
			
		}
		
		return expressionValues;
		
	}	

	
	void validateExpression(String expression) {
		
		try {
			Double.parseDouble(expression);
		}
		catch(NumberFormatException e) {
			throw new RuntimeException("Interpolating Tabular pricing function expression is not numeric [" + expression + "]");
		}
		
	}
	
	public interface RangeTableValueFormatter {

		public String formatValue(Object value);
		
	}
	
	public String findInterpolatedValueSmooth(Number value) {
		return getValue(value, INTERPOLATION_TYPE.SMOOTH);
	}
	
	public String findInterpolatedValue(Number value) {
		return getValue(value, INTERPOLATION_TYPE.LINEAR);
	}
	
	public String findValue(Number value) {
		return getValue(value, INTERPOLATION_TYPE.NONE);
	}
	
	public String getValue(Number value,INTERPOLATION_TYPE interpolationType) {
		return getValue(value, interpolationType, BOUNDARY_METHOD.FLAT,BOUNDARY_METHOD.FLAT);
	}
	
	
	public String getValue(Number value,INTERPOLATION_TYPE interpolationType, BOUNDARY_METHOD lowerBoundMethod, BOUNDARY_METHOD upperBoundMethod) {
		
		//INTERPOLATION_TYPE interpolationType = INTERPOLATION_TYPE.LINEAR;
		//BOUNDARY_METHOD lowerBoundInterpolationMethod = BOUNDARY_METHOD.FLAT;
		//BOUNDARY_METHOD upperBoundInterpolationMethod = BOUNDARY_METHOD.FLAT;
		

		//double contextKeyParameterValue = Double.parseDouble(value);
		double doubleValue = value != null ? value.doubleValue() : 0;
		
		//get all keys and expression values
		double[] keyValues = getKeyValues();
		double[] expressionValues = getExpressionValues();

				
		double interpolatedValue = InterpolatorFactory.interpolate(
			keyValues,
			expressionValues,
			doubleValue, 
			interpolationType, 
			lowerBoundMethod, 
			upperBoundMethod);
		
		String interpolatedValueString 
			= new BigDecimal(interpolatedValue).setScale(4,RoundingMode.HALF_UP).toString();

		
		return interpolatedValueString;
		
	}	
	
	public static void main(String[] args) {
		RangeTable table = new RangeTable();
		
//		table.insertValue("0.1", 25);
//		table.insertValue("0.3", 70);
//		table.insertValue("0.6296", 95);
		
		table.insertValue(0, 25);
		table.insertValue(0.3, 70);
		table.insertValue(0.6296, 95);

		System.out.println(table.findInterpolatedValue(0));
		System.out.println(table.findInterpolatedValue(0.6296));
		System.out.println(table.findInterpolatedValue(0.31));
		System.out.println(table.findInterpolatedValue(0.29));
		
		System.out.println(table.findValue(0.22));
//		System.out.println(table.findValue("0.01"));
//		System.out.println(table.findValue("10"));

	;
	}
}
