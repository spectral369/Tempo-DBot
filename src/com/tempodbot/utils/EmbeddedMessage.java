package com.tempodbot.utils;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbeddedMessage {
	
	public static MessageEmbed MessageEmbed (String title, String message) {
	   EmbedBuilder embedded =  new EmbedBuilder();
	   embedded.setColor(Color.darkGray);
	   embedded.setTitle(title);
	   embedded.setDescription(message);
	   return embedded.build();
	}

}
