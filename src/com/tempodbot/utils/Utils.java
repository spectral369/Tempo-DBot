package com.tempodbot.utils;

public class Utils {
	
	public static String getReadableTime(String time) {
		 int intTime =  Integer.parseInt(time); 
		int sec = intTime % 60;
	    int min = (intTime / 60)%60;
	    int hours = (intTime/60)/60;
	    return new String(hours+":"+min+":"+sec);

	}
	public static String getReadableTime(int intTime) {
		int sec = intTime % 60;
	    int min = (intTime / 60)%60;
	    int hours = (intTime/60)/60;
	    return new String(hours+":"+min+":"+sec);

	}

}
