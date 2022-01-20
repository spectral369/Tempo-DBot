package com.tempodbot.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	
	
	public static void writeToFile(String str) {
		File f =  new File(System.getProperty("user.home")+File.separator+"test.txt");
		FileWriter fw = null;
		try {
			fw = new FileWriter(f);
			fw.append(str);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
