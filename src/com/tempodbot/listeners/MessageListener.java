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
import com.tempodbot.handlers.DisconnectTimerTask;
import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.mediaqueue.MediaItemType;
import com.tempodbot.mediaqueue.MediaQueue;
import com.tempodbot.statics.EmbeddedMessage;
import com.tempodbot.utils.Utils;
import com.tempodbot.utils.YTSearch;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
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
	TextChannel commandChannel = null;

	List<MediaItem> queue = new LinkedList<MediaItem>();

	@Override
	public void onEvent(GenericEvent event) {

		if (event instanceof GuildVoiceLeaveEvent ev) {
			if (audioManager != null && audioManager.getConnectedChannel() != null
					&& audioManager.getConnectedChannel().getMembers().size() > 0) {
				DCTimer = new DisconnectTimerTask(audioManager, handler);
				t.schedule(DCTimer, 20000);
			}
		}

		else if (event instanceof MessageReceivedEvent messageEvent) {
			if (messageEvent.isFromType(ChannelType.PRIVATE)) {
				messageEvent.getChannel().sendMessage("This bot does not take commands from private messages !!!")
						.queue();
				return;
			}
			if (commandChannel == null && messageEvent.isFromType(ChannelType.TEXT)
					|| (messageEvent.getChannel().equals(commandChannel)
							&& messageEvent.isFromType(ChannelType.TEXT))) {
				if (!messageEvent.getMessage().getContentDisplay().startsWith("!")) {
					return;
				}
				if (commandChannel == null)
					commandChannel = messageEvent.getChannel().asTextChannel();

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
				TextChannel textChannel = messageEvent.getChannel().asTextChannel();
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
					// check if user is in channel
					voiceDisconnect(member.getVoiceState().getChannel());
					break;
				}
				case "!desc": {

					if (handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {

						int descSubstrLength = (queue.get(0).description().length() > 1023) ? 1023
								: queue.get(0).description().length();
						messageEvent.getChannel()
								.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Description",
										handler.getTrack().getInfo().title, handler.getTrack().getInfo().author,
										queue.get(0).description().substring(0, descSubstrLength)))
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
						messageEvent.getChannel()
								.sendMessageEmbeds(
										EmbeddedMessage.MessageEmbed("YT Link Description", item.name(), item.author(),
												item.duration(), item.thumbnail(), messageEvent.getAuthor().toString()))
								.queue();

						queue.add(item);
						handler.play();

					} else if (body.length() > 3 && body.length() < 45) {
						MediaQueue list = YTSearch.getVideoDetails(YTSearch.getYTLinks(body, 1));
						MediaItem item = list.get(0);
						messageEvent.getChannel()
								.sendMessageEmbeds(
										EmbeddedMessage.MessageEmbed("YT Audio Description", item.name(), item.author(),
												item.duration(), item.thumbnail(), messageEvent.getAuthor().toString()))
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

							messageEvent.getChannel()
									.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Description", item.name(),
											item.author(), item.duration(), item.thumbnail(),
											messageEvent.getAuthor().toString()))

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
				case "!resume": {
					if (handler.getPlayer().getPlayingTrack() != null
							&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						handler.getPlayer().setPaused(false);

					}
					break;
				}
				case "!time": {
					if (handler.getPlayer().getPlayingTrack() != null
							&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {
						int timeE = (int) (handler.getPlayer().getPlayingTrack().getPosition() / 1000L);
						int timeT = (int) (handler.getPlayer().getPlayingTrack().getDuration() / 1000L);
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed(/* "Time Elapsed", */
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
						messageEvent.getChannel()
								.sendMessageEmbeds(
										EmbeddedMessage
												.MessageEmbed("Description",
														item.name() + " \n " + item.author() + " \n"//
																+ item.duration(),
														item.thumbnail(), item.requestor()))
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

					if (handler.getTrack()!=null && handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {

						handler.getPlayer().stopTrack();
						if (queue.size() < 1) {
							handler.getPlayer().checkCleanup(1000);

						}
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Track Stopped!"))
								.queue();
					} else {
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Nothing to stop"))
								.queue();
					}
					break;
				}
				case "!list": {

					StringBuilder songList = new StringBuilder();
					for (MediaItem s : queue)
						songList.append(queue.indexOf(s) + " " + s.name() + "\n");

					messageEvent.getChannel()
							.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Song list", songList.toString())).queue();
					break;
				}
				case "!clear": {

					if (handler != null && handler.getPlayer() != null && handler.getTrack()!=null
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
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Volume",
								String.valueOf(handler.getPlayer().getVolume() + "%"))).queue();
					} else if (body.length() > 0) {
						int volume = 80;
						try {
							volume = Integer.parseInt(body);
						} catch (Exception err) {
							messageEvent.getChannel()
									.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Error", "Integer required !!!"))
									.queue();
						}
						if (handler != null && handler.getPlayer() != null
								&& handler.getPlayer().getPlayingTrack() != null
								&& handler.getPlayer().getPlayingTrack().getState() == AudioTrackState.PLAYING) {

							handler.getPlayer().setVolume(volume);
							messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed(
									"Volume is set to: " + String.valueOf(handler.getPlayer().getVolume()) + "%"))
									.queue();
						}
					}

					break;
				}
				case "!repeat": {

					if (queue.size() > 0 && handler.getRepeat() == false) {
						handler.setRepeat(true);
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Repeat is on"))
								.queue();
					} else if (handler.getRepeat()) {
						handler.setRepeat(false);
					}

					break;
				}
				case "!help": {

					try {
						FileReader fr = new FileReader("help.txt");
						BufferedReader br = new BufferedReader(fr);
						StringBuilder sb = new StringBuilder();
						while (br.ready())
							sb.append(br.readLine() + "\n");
						messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.MessageEmbed("Help", sb.toString()))
								.queue();
						br.close();

					} catch (IOException e) {
						System.out.println("errror: " + e.getLocalizedMessage());
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
					if (!guild.getAudioManager().isConnected()) {
						connectTo(member.getVoiceState().getChannel(), textChannel, queue);
					}
					MediaItem item = new MediaItem(MediaItemType.RADIO,
							"http://astreaming.virginradio.ro:8000/virgin_aacp_64k", message.getAuthor().getName(),
							"Virgin Radio Romania", "Live", true, "Virgin Radio Romania", "Virgin Radio",
							"https://virginradio.ro/wp-content/uploads/2019/06/VR_ROMANIA_WHITE-STAR-LOGO_RGB_ONLINE_1600x1600.png");

					queue.add(item);
					if (queue.size() > 1)
						messageEvent.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle("Virgin Radio Romania")
								.addField("Requestor", item.requestor(), true).setThumbnail(item.thumbnail()).build())
								.queue();
					if (queue.size() == 0)
						handler.play();
					break;
				}
				case "!radiozu": {

					MediaItem item = new MediaItem(MediaItemType.RADIO,
							"https://ivm.antenaplay.ro/liveaudio/radiozu/playlist.m3u8", message.getAuthor().getName(),
							"Radio ZU Romania", "Live", true, "Radio ZU Romania", "Radio ZU",
							"https://you.com/proxy?url=https%3A%2F%2Ftse2.explicit.bing.net%2Fth%3Fid%3DOIP.QiwC7hwxRHhATtQEyNg4GwAAAA%26w%3D690%26c%3D7%26pid%3DApi%26p%3D0");

					queue.add(item);
					if (handler != null)
						handler.play();

					/*
					 * messageEvent.getChannel().sendMessageEmbeds(EmbeddedMessage.
					 * MessageEmbed("Not Yet",
					 * "For the momment we cannot play m3u8/m3u streams !")).queue();
					 */
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
