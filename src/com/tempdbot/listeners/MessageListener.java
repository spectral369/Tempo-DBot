package com.tempdbot.listeners;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.tempodbot.utils.EmbeddedMessage;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * 
 * @author spectral369 This class handles messages from text channels and
 *         process them!
 *
 */

public class MessageListener implements EventListener {

	AudioManager audioManager;
	AudioHandler handler;

	List<String> queue = new LinkedList<>();

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent messageEvent) {
			if (messageEvent.isFromType(ChannelType.PRIVATE)) {
				messageEvent.getTextChannel().sendMessage("This bot does not take commands from private messages !!!")
						.queue();
				return;
			}
			if (messageEvent.isFromType(ChannelType.TEXT)) {
				if (!messageEvent.getMessage().getContentDisplay().startsWith("!")) {
					return;
				}

				User author = messageEvent.getAuthor();
				if (author.isBot()) {
					return;
				}
				if (!messageEvent.isFromGuild()) {
					return;
				}

				Message message = messageEvent.getMessage();
				// MessageChannel channel = messageEvent.getChannel();
				String msg = message.getContentDisplay();
				Guild guild = messageEvent.getGuild();
				TextChannel textChannel = messageEvent.getTextChannel();
				Member member = messageEvent.getMember();
				String name;
				if (message.isWebhookMessage()) {
					name = author.getName();
				} else {
					name = member.getEffectiveName();
				}
				System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);

				String parsedMsg = parseCmdMsg(msg);
				String body = msg.substring(parsedMsg.length()).trim();
				System.out.println(body);
				switch (parsedMsg) {
				case "!join": {
					onConnecting(messageEvent, guild, member);
					connectTo(member.getVoiceState().getChannel(), textChannel, queue);
					break;
				}
				case "!leave": {
					voiceDisconnect(member.getVoiceState().getChannel());
					break;
				}
				case "!desc": {

					if (handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						messageEvent.getChannel()
								.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Description",
										handler.getTrack().getInfo().title + " \n "
												+ handler.getTrack().getInfo().author + " "
												+ handler.getTrack().getInfo().length))
								.queue();
					}

					break;
				}
				case "!play": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!pause": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!time": {
					if (handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						int timeE =  (int) (handler.getPlayer().getPlayingTrack().getPosition() /1000L); 
						int timeT =  (int) (handler.getPlayer().getPlayingTrack().getDuration() /1000L);
						messageEvent.getChannel().sendMessageEmbeds(
								EmbeddedMessage.MessageEmbed("Time Elapsed", String.valueOf(timeE)+"/"+String.valueOf(timeT)))
								.queue();
					}
					
					
					
					break;
				}
				case "!add": {
					if (body.isBlank() || body.isEmpty()) {
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("YT link required!"))
								.queue();
						return;
					}
					if (body.matches("^(http(s)://)?((w){3}.)?youtu(be|.be)?(.com)?/.+")) {
						queue.add(body);
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("YT audio added!"))
								.queue();
					} else {
						messageEvent.getChannel().sendMessageEmbeds(
								EmbeddedMessage.MessageEmbed("YT link is not right", "Please provide the correct url."))
								.queue();
					}

					break;
				}
				case "!remove": {
					if (queue.size() <= 0) {
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Nothing to remove"))
								.queue();
						return;
					}
					queue.remove(0);
					messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Item at index 0 removed"))
							.queue();
					break;
				}
				case "!skip": {

					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!stop": {

					if (handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {

						handler.getPlayer().stopTrack();
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Track Stopped!"))
								.queue();
					} else {
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Nothing to stop"))
								.queue();
					}

					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!list": {

					List<String> list = new LinkedList<>();
					for (String s : queue)
						list.add(list.indexOf(s) + " " + s);

					messageEvent.getChannel()
							.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Song list", (String[]) list.toArray()))
							.queue();
					break;
				}
				case "!clear": {

					if (handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						handler.getPlayer().stopTrack();
					}
					queue.clear();

					messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Clear done")).queue();
					break;
				}
				case "!move": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!shuffle": {
					Collections.shuffle(queue);
					messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Shuffle done !")).queue();
					break;
				}
				case "!volume": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!repeat": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!test": {
					if (!guild.getAudioManager().isConnected()) {
						// onConnecting(messageEvent, guild, member);
						connectTo(member.getVoiceState().getChannel(), textChannel, queue);
					}
					String no[] = msg.split(" ");
					if (msg.contains("tub")) {
						queue.add(no[1]);
						handler.play();
					} else {

					}

				}

				default:
					return;

				}

			}

		}

	}

	private void onConnecting(MessageReceivedEvent event, Guild guild, Member member) {
		AudioChannel channel = member.getVoiceState().getChannel();
		if (channel == null)
			return;
		else {
			event.getChannel().sendMessage("Connection" + channel.getName()).queue();
		}

	}

	private void connectTo(AudioChannel channel, TextChannel txtChannel, List<String> queue) {

		Guild guild = channel.getGuild();
		audioManager = guild.getAudioManager();
		if (audioManager.isConnected())
			return;
		else {
			handler = new AudioHandler(guild, queue, txtChannel);
			audioManager.setSendingHandler(handler);
			audioManager.openAudioConnection(channel);
		}
	}

	private void voiceDisconnect(AudioChannel channel) {
		Guild guild = channel.getGuild();
		audioManager = guild.getAudioManager();
		audioManager.closeAudioConnection();
	}

	private String parseCmdMsg(String msg) {
		String cmd = null;
		if (msg.matches("^(!\\w+)")) {

		}
		Pattern cmp = Pattern.compile("^(!\\w+)");
		Matcher mch = cmp.matcher(msg);
		if (mch.find()) {
			cmd = mch.group(1);
		}

		return cmd;
	}

}
