package org.cehl.cehltools.rerate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RerateUtils {
	
	public static int roundUpToInt(double d) {
	    return roundToInt(d, RoundingMode.HALF_UP);
	}
	
	public static int roundToInt(double d, RoundingMode roundingMode) {
	   // BigDecimal bd = new BigDecimal(Double.toString(d));
		BigDecimal bd = new BigDecimal(d);
	    bd = bd.setScale(0, roundingMode);
	    return bd.intValue();
	}
	
	
	public static int normalizeRating(double rating) {
		
		int normalized = roundUpToInt(rating);
		
		normalized = Math.min(normalized, 99);
		normalized = Math.max(normalized, 25);
		
		return normalized;
	}
	
	public static int normalizeRating(int rating) {
		
		int normalized = rating;
		
		normalized = Math.min(normalized, 99);
		normalized = Math.max(normalized, 25);
		
		return normalized;
	}
	
	
    public static String convertToCSV(String[] data) {
        return Stream.of(data)
            .map(string-> escapeSpecialCharacters(string))
            .collect(Collectors.joining(","));
    }

    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    
    public static int calculateAge(
    		  LocalDate birthDate) {
    		    return Period.between(birthDate, LocalDate.now()).getYears();
    		}
    
    public static double lerp(double point1, double point2, double alpha) {
    	return point1 + alpha * (point2-point1);
    }
    
	/**
	 * Calculates the weighted average of a map.
	 * 
	 *  Map<Double, Integer> map = new HashMap<>();
     map.put(0.7, 100);
     map.put(0.5, 200);
     Double weightedAverage = calculateWeightedAverage(map);
     Assert.assertTrue(weightedAverage.equals(0.5666666666666667));
	 *
	 * @throws ArithmeticException If divide by zero happens
	 * @param map A map of values and weights
	 * @return The weighted average of the map
	 */
	public static Double calculateWeightedAverage(Map<Double, Integer> map) throws ArithmeticException {
		double num = 0;
		double denom = 0;
		for (Map.Entry<Double, Integer> entry : map.entrySet()) {
			num += entry.getKey() * entry.getValue();
			denom += entry.getValue();
		}

		return num / denom;
	}
	
	public static void addToAverageMap(Map<Double, Integer> map, double rating, int weight) {
		
		if(map.containsKey(rating)) {
			int newWeight = map.get(rating) + weight;
			map.put(rating, newWeight);
			
		}else {
			map.put(rating, weight);
		}
		
	}
	
	public static double calculateOv(String position, double it, double sp, double st, double en, double du, double di, double sk, double pa, double pc, double df, double sc, double ex, double ld) {
		
		if(position.contains("D")) {
			return calculateDefenseOv(it, sp, st, en, du, di, sk, pa, pc, df, sc, ex, ld);
		}else if(position.contains("G")) {
			return calculateGoalieOv(it, sp, st, en, du, di, sk, pa, pc, df, sc, ex, ld);
		}else {
			return calculateForwardOv(it, sp, st, en, du, di, sk, pa, pc, df, sc, ex, ld);
		}
	}
	
	public static double calculateForwardOv(double it, double sp, double st, double en, double du, double di, double sk, double pa, double pc, double df, double sc, double ex, double ld) {
		double value = ((it*0.0735)
				+(sp*0.0735)
				+(st*0.103)
				+(en*0.044)
				+(du*0.0148)
				+(di*0.0147)
				+(sk*0.089)
				+(pa*0.147)
				+(pc*0.1175)
				+(df*0.1025)
				+(sc*0.191)
				+(ex*0.0147)
				+(ld*0.0148)
				+(1*4.505));
		
		return  Math.round(value);
	}
	
	public static double calculateDefenseOv(double it, double sp, double st, double en, double du, 
			double di, double sk, double pa, double pc, double df, double sc, double ex, double ld) {

		double value = 0;
		
		if ((pa+sc)>=(df+st)) {
			value = ((it*0.059)
	   				+(sp*0.1175)
	   				+(st*0.059)
	   				+(en*0.044)
	   				+(du*0.0148)
	   				+(di*0.0147)
	   				+(sk*0.1175)
	   				+(pa*0.162)
	   				+(pc*0.132)
	   				+(df*0.089)
	   				+(sc*0.161)
	   				+(ex*0.0147)
	   				+(ld*0.0148)
	   				+(1*4.51));
		}else {
			value = ((it*0.1175)
					+(sp*0.0735)
					+(st*0.147)
					+(en*0.044)
					+(du*0.0148)
					+(di*0.0147)
					+(sk*0.0885)
					+(pa*0.118)
					+(pc*0.088)
					+(df*0.191)
					+(sc*0.0735)
					+(ex*0.0147)
					+(ld*0.0148)
					+(1*4.505));
		}

		return Math.round(value);
	}
	
	public static double calculateGoalieOv(double it, double sp, double st, double en, double du, double di, double sk, double pa, double pc, double sc, double df, double ex, double ld) {
		double value = ((it*0.0945)
				+(sp*0.3215)
				+(st*0.0755)
				+(en*0.0565)
				+(du*0.0188)
				+(di*0.0187)
				+(sk*0.1325)
				+(pa*0.0565)
				+(pc*0.151)
				+(ex*0.0187)
				+(ld*0.0188)
				+(-1*0.475));

		
		return  Math.round(value);
	}
}
