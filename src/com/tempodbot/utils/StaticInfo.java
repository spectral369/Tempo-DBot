package com.tempodbot.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * @author spectral369
 * @description
 * Static info class
 * Here we should put static information like static Strings/configs
 * 
 * 
 */
public enum StaticInfo {

   DISCORD_TOKEN("OTIwOTkwNzUwMDMwODQ4MDMw.YbsZeQ.vFoP1MTa7eAT8apSpMs__zotEL4");
	private static final Map<String, StaticInfo> DATA =  new  HashMap<>();
	
	private final String value;
	
	StaticInfo(String value) {
		this.value = value;
	}
	
	static {
		for(StaticInfo e: values()) {
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
