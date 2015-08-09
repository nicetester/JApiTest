package com.penngo.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {
	public final static String DATA_PATH = "/data";
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
	public static String getExportPath(){
		String pattern = "yyyy/MM/dd";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	public static void main(String[] args){
//		System.out.println(calTime(100000));
	}
}
