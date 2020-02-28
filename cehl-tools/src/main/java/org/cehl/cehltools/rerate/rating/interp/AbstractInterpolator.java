package org.cehl.cehltools.rerate.rating.interp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractInterpolator implements Interpolator {

	private static final Logger logger = LoggerFactory.getLogger(AbstractInterpolator.class);
	
	public enum INTERPOLATION_TYPE{
		NONE,
		LINEAR,
		SMOOTH
	};
	
	public enum BOUNDARY_METHOD{FLAT,LINEAR,PROPORTIONAL};
	
	protected BOUNDARY_METHOD lowerBoundMethod = BOUNDARY_METHOD.FLAT;
	protected BOUNDARY_METHOD upperBoundMethod = BOUNDARY_METHOD.FLAT;
	
	protected double[] y = null;          // y=f(x) tabulated function
	protected double[] x = null;          // x in tabulated function f(x)

	public abstract double interpolate(double xvalue);
	
	public abstract INTERPOLATION_TYPE getInterpolationType();
	
	int lowerBoundXValueIndex(double xx) {
		
		//find first x value that is lower than the interpolation value
		for(int i=x.length-1; i >=0; i--) {
			if(x[i] <= xx) {
				return i;
			}
		}

		//lower bound does not exist
		return -1;
		
	}
	
	public BOUNDARY_METHOD getLowerBoundMethod() {
		return lowerBoundMethod;
	}

	public void setLowerBoundMethod(BOUNDARY_METHOD lowerBoundMethod) {
		this.lowerBoundMethod = lowerBoundMethod;
	}

	public BOUNDARY_METHOD getUpperBoundMethod() {
		return upperBoundMethod;
	}

	public void setUpperBoundMethod(BOUNDARY_METHOD upperBoundMethod) {
		this.upperBoundMethod = upperBoundMethod;
	}

	//xx is the x-axis point less than the first x data point
	double interpolateLowerBound(double xx) {
		
		double interpolatedValue = 0;
		
		switch(lowerBoundMethod) {
		case FLAT:
			interpolatedValue = y[0];
			break;
		//LINEAR and PROPORTIONAL are equivalent for the lower bound because it must
		//trend down to the origin
		case LINEAR:
		case PROPORTIONAL:
			double slope = slopeOfLowerBoundary();
			double yDelta = (x[0]-xx) * slope;
			interpolatedValue = y[0] + yDelta;
		 	break;
		}
		
		return interpolatedValue;
		
	}

	//xx is the x-axis point beyond the largest x data point
	double interpolateUpperBound(double xx) {

		double interpolatedValue = 0;
		
		double yDelta = 0;
		switch(upperBoundMethod) {
			case FLAT:
				interpolatedValue = y[y.length-1];
				break;
			case LINEAR:
				double slopeOfLastSegment = slopeOfLastSegment();
//				if(slopeOfLastSegment < 0) {
//					//trend from last y value toward x-axis without crossing
//					interpolatedValue = y[y.length-1] / (xx-x[x.length-1]+1);
//				}
//				else {
//					double yDelta = (xx-x[x.length-1]) * slopeOfLastSegment;
//					interpolatedValue = y[y.length-1] + yDelta;
//				}
				yDelta = (xx-x[x.length-1]) * slopeOfLastSegment;
				interpolatedValue = y[y.length-1] + yDelta;
				break;
			case PROPORTIONAL:
				//use the ratio of the last data points as the slope
				yDelta = (xx-x[x.length-1]) * proportionalUpperBoundSlope();
				interpolatedValue = y[y.length-1] + yDelta;
				break;
		}

		return interpolatedValue;
		
	}
	
	double slopeOfLastSegment() {
		//TODO: this won't work if there is less than two x values
		return slope(x.length-2,x.length-1);
	}

	double slopeOfFirstSegment() {
		return slope(0,1);
	}	
	
	double slopeOfLowerBoundary() {
		
		double slopeOfFirstSegment = slope(0,1);
		
		double slope = 0;
		if(slopeOfFirstSegment > 0) {
			//if slope is positive, trend down to origin
			//slope = x[0] > 0 ? y[0]/x[0] : 0;
			slope = proportionalLowerBoundSlope();
		}
		else {
			//slope is negative
			slope = slopeOfFirstSegment;
		}
		
		return slope * -1;
		
	}
	
	double slope(int x1,int x2) {
		return (y[x2] - y[x1])/Math.abs(x[x2] - x[x1]); 
	}
	
	double proportionalUpperBoundSlope() {
		
		int upperIndex = x.length-1;
		double slope = x[upperIndex] > 0 ? y[upperIndex]/x[upperIndex] : 0;
		
		double direction = slopeOfLastSegment() >=0 ? 1 : -1;
		
		return slope * direction;

	}
	
	double proportionalLowerBoundSlope() {
		return x[0] > 0 ? y[0]/x[0] : 0;
	}
	
//	//perform a digital integration over the interpolated curve
//	public double integrate(double xStart,double xEnd,double delta) {
//		
//		double xx = xStart;
//		double totalArea = 0;
//		while(xx <= xEnd) {
//			
//			totalArea += (delta * interpolate(xx));
//			xx += delta;
//			
//		}
//		
//		return totalArea;
//		
//	}
	
//	//perform a digital integration over the interpolated curve
//	public double integrate(double xStart,double xEnd,double delta) {
//
//		//delta cannot exceed the x span
//		if(delta > (xEnd-xStart)) {
//			delta = (xEnd-xStart);
//		}
//		
//		double xx = xStart;
//		double totalArea = 0;
//		double adjustedDelta=0;
//		while(xx < xEnd) {
//				
//			//ensure that the delta never exceeds the upper boundary (xEnd)
//			adjustedDelta = (delta <= (xEnd-xx)) ? delta : xEnd-xx;
//
//			totalArea += (adjustedDelta * interpolate(xx));
//			
//			xx += adjustedDelta;
//			
//		}
//		
//		return totalArea;
//		
//	}

	//perform a digital integration over the interpolated curve
	public double integrate(double xStart,double xEnd) {

		double delta = (xEnd-xStart)/MAX_INTEGRATION_DELTAS;
		
		double xx = xStart;
		double totalArea = 0;
		double adjustedDelta=0;
		while(xx < xEnd) {
				
			//ensure that the delta never exceeds the upper boundary (xEnd)
			adjustedDelta = (delta <= (xEnd-xx)) ? delta : xEnd-xx;

			totalArea += (adjustedDelta * interpolate(xx));
			
			xx += adjustedDelta;
			
		}
		
		return totalArea;
		
	}
	
	
	@Override
	public boolean isExtrapolated(double xvalue) {
		return xvalue > x[x.length-1] || lowerBoundXValueIndex(xvalue) == -1;
	}
	
}
