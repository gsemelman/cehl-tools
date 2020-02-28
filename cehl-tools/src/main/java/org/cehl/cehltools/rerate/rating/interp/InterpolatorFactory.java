package org.cehl.cehltools.rerate.rating.interp;

import org.cehl.cehltools.rerate.rating.interp.AbstractInterpolator.BOUNDARY_METHOD;
import org.cehl.cehltools.rerate.rating.interp.AbstractInterpolator.INTERPOLATION_TYPE;

public class InterpolatorFactory {

	public static Interpolator getInstance(
			INTERPOLATION_TYPE type,
			BOUNDARY_METHOD lowerBoundMethod,
			BOUNDARY_METHOD upperBoundMethod,
			double xValues[],
			double yValues[]) {

		Interpolator interpolator = null;

		//override interpolator type, as required, based on available data points
		//can't do smooth interpolation with less than three data points
		if(xValues.length == 1 || INTERPOLATION_TYPE.NONE.equals(type)) {
			interpolator = new ConstantValueInterpolator(xValues,yValues);
		}
		else {
			interpolator = new CommonsInterpolator(
				xValues.length < 3 ? INTERPOLATION_TYPE.LINEAR : type,
				xValues,
				yValues);
		}
		
		interpolator.setLowerBoundMethod(lowerBoundMethod);
		interpolator.setUpperBoundMethod(upperBoundMethod);
		
		return interpolator;
		
	}
	
	public static double interpolate(
		double[] xvalues,
		double[] yvalues,
		double xvalue,
		INTERPOLATION_TYPE interpolationType,
		BOUNDARY_METHOD lowerBoundInterpolationMethod,
		BOUNDARY_METHOD upperBoundInterpolationMethod) {
				
		Interpolator interpolator = InterpolatorFactory.getInstance(
				interpolationType, 
				lowerBoundInterpolationMethod, 
				upperBoundInterpolationMethod, 
				xvalues, 
				yvalues);

		return interpolator.interpolate(xvalue);
		
	}
	
}
