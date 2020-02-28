package org.cehl.cehltools.rerate.rating.interp;

public class LinearInterpolator extends AbstractInterpolator {
		
	public LinearInterpolator(double[] x, double[] y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public double interpolate(
			double x1,double x2,double x3,double y1,double y3) {

		//(slope * relative x offset) added to y1
		return ( (y3-y1)/(x3-x1) * (x2-x1) ) + y1;
		
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
			interpolatedValue = interpolate(
					x[lowerBoundXValueIndex],
					xx,
					x[lowerBoundXValueIndex+1],
					y[lowerBoundXValueIndex],
					y[lowerBoundXValueIndex+1]);
		}
		
		return interpolatedValue;
		
	}
	
	@Override
	public INTERPOLATION_TYPE getInterpolationType() {
		return INTERPOLATION_TYPE.LINEAR;
	}
	
}
