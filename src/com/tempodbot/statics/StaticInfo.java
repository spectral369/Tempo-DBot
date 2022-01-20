package com.tempodbot.statics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author spectral369
 * @description Static info class Here we should put static information like
 *              static Strings/configs
 * 
 * 
 */
public enum StaticInfo {

	DISCORD_TOKEN("");

	private static final Map<String, StaticInfo> DATA = new HashMap<>();

	private String value;

	StaticInfo(String value) {

		FileReader fr;
		BufferedReader br;
		try {

			File tokenFile = new File(System.getProperty("user.home")+File.separator+"token.txt");
			if (!tokenFile.exists()) {
				FileWriter fw = new FileWriter(tokenFile);
				fw.write("<Insert token here>");
				fw.close();
			} else {
				fr = new FileReader(tokenFile);
				br = new BufferedReader(fr);
				this.value = br.readLine().trim();
			}
		} catch (IOException e) {

			System.out.println(e.getLocalizedMessage());
		}

	}

	static {
		for (StaticInfo e : values()) {
			DATA.put(e.toString(), e);
		}
	}

	public static String valueOfStr(StaticInfo name) {
		return DATA.get(name.toString()).value;
	}

	public String getVal() {
		return value;
	}

}
