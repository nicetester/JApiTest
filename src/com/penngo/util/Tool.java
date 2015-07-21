package com.penngo.util;

import java.math.BigDecimal;

public class Tool {
	public static String calTime(long time){
		String str = time + " ms";
		BigDecimal t1 = new BigDecimal(time);
		if(time > 100){
			BigDecimal t2 = new BigDecimal(1000);
			t1 = t1.divide(t2, 2, BigDecimal.ROUND_HALF_UP);
			str = t1.doubleValue() + " s";
			
			if(t1.longValue() > 60){
				t2 = new BigDecimal(60);
				t1 = t1.divide(t2, 2, BigDecimal.ROUND_HALF_UP);
				str = t1.doubleValue() + " min";
			}
		}
		
		return str;
	}
	
	public static void main(String[] args){
//		System.out.println(calTime(100000));
	}
}
