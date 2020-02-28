package org.cehl.cehltools.rerate.rating.interp;

public class CubicSplineInterpolator extends AbstractInterpolator {

	//private CubicSpline cubicSpline;
	
	public CubicSplineInterpolator(double[] x,double[] y) {

		this.x = x;
		this.y = y;

		//cubicSpline = new CubicSpline(x,y);
		
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
			//interpolatedValue = cubicSpline.interpolate(xx);
		}
		
		return interpolatedValue;
		
	}

	@Override
	public INTERPOLATION_TYPE getInterpolationType() {
		return INTERPOLATION_TYPE.SMOOTH;
	}
		
}
