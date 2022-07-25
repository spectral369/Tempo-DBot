package com.tempodbot.statics;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbeddedMessage {

	final static String[] info = { "Title", "Author", "Length", "Requestor" };

	public static MessageEmbed MessageEmbed(String... args) {
		EmbedBuilder embedded = new EmbedBuilder();
		embedded.setColor(Color.darkGray);
		if (args.length > 2)
			embedded.setTitle(args[0]);
		String thumbnail = null;
		boolean toInline = false;
		int i = 0;
		if (args.length > 2) {
			for (String s : args) {
				if (s.equals(args[0]))
					continue;
				if (i > 1)
					toInline = true;
				if (s.contains("ytimg")) {
					String r = s.substring(0, s.indexOf(".jpg?") + 4);
					String q = r.substring(r.indexOf("vi/") + 3);
					thumbnail = q.substring(0, q.indexOf("/"));

					continue;
				}

				embedded.addField(info[i], s, toInline);
				i++;
			}

			embedded.setThumbnail("https://img.youtube.com/vi/" + thumbnail + "/default.jpg");
		} else {
			for (String s : args) {
				embedded.addField("",s, false);
			}
		}

		return embedded.build();
	}

	public static MessageEmbed MessageEmbed(String str, String[] str2) {
		EmbedBuilder embedded = new EmbedBuilder();
		embedded.setColor(Color.darkGray);
		embedded.setTitle(str);
		StringBuilder sb = new StringBuilder();
		for (String s : str2) {
			sb.append(s + "\n");
		}
		embedded.setDescription(sb.toString());
		return embedded.build();
	
	}

	public static MessageEmbed MessageEmbed(AudioTrack track) {
		EmbedBuilder embed =  new EmbedBuilder();
		
		embed.setTitle("▶️ Now Playing");
		embed.addField("Track Name", "[" + track.getInfo().title + "](" + track.getInfo().uri + ")", true);
			embed.addField("Length",
					String.format("%02d min %02d sec", track.getDuration() / TimeUnit.MINUTES.toMillis(1),
							track.getDuration() % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1)),
					true);
		embed.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/default.jpg");
		
		return embed.build();
		
	}

}
