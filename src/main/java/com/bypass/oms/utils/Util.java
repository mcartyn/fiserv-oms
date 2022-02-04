package com.bypass.oms.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {

	public static float percentReduction (float x, float percent) {
//		x = roundOff(x - x*percent); 
		return x - x*percent; 
	}
	
	public static float percentAddition (float x, float percent) {
		x = x + x*percent; 
		return x; 
	}
	
	public static float percent (float x, float percent) {
		return x*percent; 
//		return x; 
	}
	
	public static float roundOff (float x) {
//		return x; 
		BigDecimal number = new BigDecimal(x);
		return number.setScale(0, RoundingMode.UP).intValue();
	}
}
