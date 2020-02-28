package org.cehl.cehltools.rerate.rating.interp;

//generates a sawtooth graph
//assume y-value(X to X+1) = y-value(X+1) 
//the other rule is: y-value(X to X+1) = y-value(X)
//this behaviour could be parameterized
//Boundary methods do not apply for a constant value interpolator
public class ConstantValueInterpolator extends AbstractInterpolator {
		
	public ConstantValueInterpolator(double[] x, double[] y) {
		
		this.x = x;
		this.y = y;
		
	}

	//boundary methods do not apply to this interpolator
	public double interpolate(double xx) {
		
		//handle empty range array
		if(y.length == 0) return 0;
		
		int xRangeIndex = 0;
		if(xx > x[x.length-1]) {
			xRangeIndex = x.length-1;
		}
		else {
			for(int i=0; i < x.length; i++) {
				if(xx <= x[i]) {
					xRangeIndex = i;
					break;
				}
			}
		}
		
		return y[xRangeIndex];
		
	}
	
	@Override
	public INTERPOLATION_TYPE getInterpolationType() {
		return INTERPOLATION_TYPE.NONE;
	}
	
}
