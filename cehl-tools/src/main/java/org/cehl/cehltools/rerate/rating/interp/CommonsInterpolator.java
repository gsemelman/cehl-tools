package org.cehl.cehltools.rerate.rating.interp;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator;


public class CommonsInterpolator extends AbstractInterpolator {
			
	INTERPOLATION_TYPE type;	
	UnivariateRealFunction function=null;
	
	public CommonsInterpolator(INTERPOLATION_TYPE type,double[] x, double[] y) {
		
		this.type = type;
		
		this.x = x;
		this.y = y;
		
		UnivariateRealInterpolator interpolator = null;
		
		switch(type) {
			case LINEAR:
				interpolator = new org.apache.commons.math.analysis.interpolation.LinearInterpolator();
				break;
			case SMOOTH:
				interpolator = new SplineInterpolator();
				break;
		}
		
		try {
			function = interpolator.interpolate(x,y);
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
		
	}
		
	public double interpolate(double xx) {
		
		//handle empty range array
		if(y.length == 0) return 0;
		
		int lowerBoundXValueIndex = lowerBoundXValueIndex(xx);
			
		double interpolatedValue = 0;
		if(lowerBoundXValueIndex == -1) {
			interpolatedValue = interpolateLowerBound(xx);			
		}
		else if(xx > x[x.length-1]) {			
			interpolatedValue = interpolateUpperBound(xx);			
		}
		else {
			try {
				interpolatedValue = function.value(xx);
			} catch (FunctionEvaluationException e) {
				throw new RuntimeException(e);
			}
		}
		
		return interpolatedValue;
		
	}
	
	@Override
	public INTERPOLATION_TYPE getInterpolationType() {
		return type;
	}
	
	
}
