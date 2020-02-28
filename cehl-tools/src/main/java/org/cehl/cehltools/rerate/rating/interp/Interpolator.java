package org.cehl.cehltools.rerate.rating.interp;

import org.cehl.cehltools.rerate.rating.interp.AbstractInterpolator.BOUNDARY_METHOD;
import org.cehl.cehltools.rerate.rating.interp.AbstractInterpolator.INTERPOLATION_TYPE;

public interface Interpolator {

	public final int MAX_INTEGRATION_DELTAS = 10000;
	
	public double interpolate(double xvalue);
	
	public void setLowerBoundMethod(BOUNDARY_METHOD lowerBoundMethod);
	public BOUNDARY_METHOD getLowerBoundMethod();
	public void setUpperBoundMethod(BOUNDARY_METHOD upperBoundMethod);
	public BOUNDARY_METHOD getUpperBoundMethod();
	
	public INTERPOLATION_TYPE getInterpolationType();	
	
	//public double integrate(double xStart,double xEnd,double delta);
	
	//delta parameter has been eliminated, we will always use the largest number of 
	//deltas (MAX_INTEGRATION_DELTAS) when performing a digital integration
	public double integrate(double xStart,double xEnd);

	//returns true if the xvalue is in a boundary range (< first data point OR > last data point)
	public boolean isExtrapolated(double xvalue);
	
}
