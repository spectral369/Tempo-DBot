package com.tempodbot.statics;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbeddedMessage {
	
	public static MessageEmbed MessageEmbed (String ...args) {
	   EmbedBuilder embedded =  new EmbedBuilder();
	   embedded.setColor(Color.darkGray);
	   embedded.setTitle(args[0]);
	   StringBuilder sb = new StringBuilder();
	   for(String s:args) {
		   if(s.equals(args[0]))
			   continue;
		   sb.append(s+"\n");
	   }
	   embedded.setDescription(sb.toString());
	   return embedded.build();
	}
	public static MessageEmbed MessageEmbed (String str, String[] str2) {
		   EmbedBuilder embedded =  new EmbedBuilder();
		   embedded.setColor(Color.darkGray);
		   embedded.setTitle(str);
		   StringBuilder sb = new StringBuilder();
		   for(String s:str2) {
			   sb.append(s+"\n");
		   }
		   embedded.setDescription(sb.toString());
		   return embedded.build();
		}

}
