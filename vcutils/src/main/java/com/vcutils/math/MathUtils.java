package com.vcutils.math;

public class MathUtils {

	public static float lerp(float start, float end, float percent)
	{
	     return (start + percent * (end - start));
	}
	
}
