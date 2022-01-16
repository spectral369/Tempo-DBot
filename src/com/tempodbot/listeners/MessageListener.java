package com.tempodbot.listeners;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.mediaqueue.MediaItemType;
import com.tempodbot.mediaqueue.MediaQueue;
import com.tempodbot.utils.DisconnectTimerTask;
import com.tempodbot.utils.EmbeddedMessage;
import com.tempodbot.utils.Utils;
import com.tempodbot.utils.YTSearch;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
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
	DisconnectTimerTask DCTimer;
	Timer t = new Timer(true);
	

	List<MediaItem> queue = new LinkedList<MediaItem>();

	@Override
	public void onEvent(GenericEvent event) {

		if (event instanceof GuildVoiceLeaveEvent ev) {

			DCTimer = new DisconnectTimerTask(audioManager);
			t.schedule(DCTimer, 20000);
		}

		else if (event instanceof MessageReceivedEvent messageEvent) {
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
				System.out.println("Body: " + body);
				switch (parsedMsg) {
				case "!join": {
					onConnecting(messageEvent, guild, member);
					connectTo(member.getVoiceState().getChannel(), messageEvent.getChannel(), queue);
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
												+ queue.get(0).description()))
								.queue();
					}

					break;
				}
				case "!play": {
					if (!guild.getAudioManager().isConnected()) {
						connectTo(member.getVoiceState().getChannel(), textChannel, queue);
					}

					if (body.isBlank() || body.isEmpty()) {
						if (queue.isEmpty())
							messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Nothing to play"))
									.queue();
						else if (queue.size() > 0) {

							handler.play();
						}

					} else if ((body.matches("^(http(s)://)?((w){3}.)?youtu(be|.be)?(.com)?/.+"))) {
						MediaQueue list = YTSearch.getVideoDetails(body);
						MediaItem item = list.get(0);
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Description",
								item.name() + " \n " + item.author() + " \n"//
										+ item.duration() + " \n" + item.thumbnail() + " \n" + item.requestor()))
								.queue();
						queue.add(item);
						handler.play();
					} else if (body.length() > 3 && body.length() < 45) {
						MediaQueue list = YTSearch.getVideoDetails(YTSearch.getYTLinks(body, 1));
						MediaItem item = list.get(0);
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Description",
								item.name() + " \n " + item.author() + " \n"//
										+ item.duration() + " \n" + item.thumbnail() + " \n" + item.requestor()))
								.queue();

						queue.add(item);
						handler.play();
					}
					break;
				}
				case "!search": {
					if (!body.isBlank() || !body.isEmpty()) {
						MediaQueue list = YTSearch.getVideoDetails(YTSearch.getYTLinks(body, 3));
						for (int i = 0; i < 3; i++) {

							MediaItem item = list.get(i);

							messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Description",
									item.name() + " \n " + item.author() + " \n"//
											+ item.duration() + " \n" + item.thumbnail() + " \n" + item.requestor()))

									.queue();
						}

					}
					break;
				}
				case "!pause": {	
					if (handler.getPlayer().getPlayingTrack() != null
							&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						handler.getPlayer().setPaused(true);

					}
					break;
				}
				case "!time": {
					if (handler.getPlayer().getPlayingTrack() != null
							&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						int timeE = (int) (handler.getPlayer().getPlayingTrack().getPosition() / 1000L);
						int timeT = (int) (handler.getPlayer().getPlayingTrack().getDuration() / 1000L);
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Time Elapsed",
								Utils.getReadableTime(timeE) + "/" + Utils.getReadableTime(timeT))).queue();
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

						MediaQueue list = YTSearch.getVideoDetails(body);
						MediaItem item = list.get(0);
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Description",
								item.name() + " \n " + item.author() + " \n"//
										+ item.duration() + " \n" + item.thumbnail() + " \n" + item.requestor()))
								.queue();
						queue.add(item);
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
					if (handler.getPlayer().getPlayingTrack() != null
							&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						handler.getPlayer().stopTrack();

					}

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
					break;
				}
				case "!list": {

					List<String> list = new LinkedList<>();
					for (MediaItem s : queue)
						list.add(String.valueOf(queue.indexOf(s)) + " " + s.name());

					messageEvent.getChannel()
							.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Song list", (String[]) list.toArray()))
							.queue();
					break;
				}
				case "!clear": {

					if (handler != null && handler.getPlayer() != null
							&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
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
					
					if (body.isBlank() || body.isEmpty()) {
						messageEvent.getChannel().sendMessageEmbeds(
								EmbeddedMessage.MessageEmbed("Volume", String.valueOf(handler.getPlayer().getVolume())))
								.queue();
					}else if(body.length()>0) {
						int volume = 80;
						try {
						 volume =  Integer.parseInt(body);
						}catch(Exception err) {
							messageEvent.getChannel().sendMessageEmbeds(
									EmbeddedMessage.MessageEmbed("Error", "Integer required !!!"))
									.queue();
						}
						if (handler.getPlayer().getPlayingTrack() != null
								&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
							
							handler.getPlayer().setVolume(volume);

						}
					}
					
					break;
				}
				case "!repeat": {
					
					
					if(queue.size()>0 && handler.getRepeat() == false) {
						handler.setRepeat(true);
						messageEvent.getChannel().sendMessageEmbeds(
								EmbeddedMessage.MessageEmbed("Repeat is on"))
								.queue();
					}else if(handler.getRepeat()) {
						handler.setRepeat(false);
					}
						
						
					break;
				}
				case "!help": {
					
					try {
						FileReader fr =  new FileReader("help.txt");
						BufferedReader br =  new BufferedReader(fr);
						StringBuilder sb =  new StringBuilder();
						while(br.ready())
							sb.append(br.readLine());
						messageEvent.getChannel().sendMessageEmbeds(
								EmbeddedMessage.MessageEmbed("Help",sb.toString()))
								.queue();
						br.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("errror: "+e.getLocalizedMessage());
					}
					
					
					break;
				}
				case "!fs": {
					if (handler.getPlayer().getPlayingTrack() != null
							&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						handler.getPlayer().stopTrack();

					}

					break;
				}
				case "!radiovirgin": {

					MediaItem item = new MediaItem(MediaItemType.RADIO,
							"http://astreaming.virginradio.ro:8000/virgin_aacp_64k", message.getAuthor().getName(),
							"Virgin Radio Romania", "Live", true, "Virgin Radio Romania", "Virgin Radio",
							"https://eu-browse.startpage.com/av/anon-image?piurl=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2Fd%2Fd1%2FVirginRadio.png%2F220px-VirginRadio.png&sp=1641801974T74080f4eb35bc36e2b096d18ce5f053eb17dc85b0e9fb17e5a56f508b600bcd0");

					queue.add(item);
					if (queue.size() > 0)
						handler.play();
					break;
				}
				case "!radiozu": {
					/*
					 * MediaItem item = new MediaItem(MediaItemType.RADIO,
					 * "https://ivm.antenaplay.ro/liveaudio/radiozu/playlist.m3u8",
					 * message.getAuthor().getName(), "Radio ZU Romania", "Live", true,
					 * "Radio ZU Romania", "Radio ZU",
					 * "https://eu-browse.startpage.com/av/anon-image?piurl=https%3A%2F%2Fwww.listenonlineradio.com%2Fwp-content%2Fuploads%2FRadio-ZU.jpg&sp=1641804927T5cfb4c4fda65544b9386b2a6f014e7533e9d6d0fa2a0aae7981e056ded44e413"
					 * );
					 * 
					 * queue.add(item); if(handler != null) handler.play();
					 */
					messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Not Yet",
							"For the momment we cannot play m3u8/m3u streams !")).queue();
					break;
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

	private void connectTo(AudioChannel channel, MessageChannel messageChannel, List<MediaItem> queue) {
		if (channel == null) {
			messageChannel.sendMessage("You're not in a voice channel").queue();
			return;
		}
		Guild guild = channel.getGuild();
		audioManager = guild.getAudioManager();
		if (audioManager.isConnected())
			return;
		else {
			handler = new AudioHandler(guild, queue, messageChannel);
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
