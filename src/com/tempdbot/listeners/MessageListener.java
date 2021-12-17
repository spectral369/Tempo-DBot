package com.tempdbot.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent messageEvent) {
			if (messageEvent.isFromType(ChannelType.PRIVATE)) {
				messageEvent.getTextChannel().sendMessage("This bot does not take commands from private messages !!!").queue();
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

				switch (parsedMsg) {
				case "!join": {
					onConnecting(messageEvent, guild, member);
					connectTo(member.getVoiceState().getChannel());
					break;
				}
				case "!leave": {
					voiceDisconnect(member.getVoiceState().getChannel());
					break;
				}
				case "!desc": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
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
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!add": {

					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!remove": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
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
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!list": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!clear": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!move": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
					break;
				}
				case "!shuffle": {
					messageEvent.getChannel().sendMessageEmbeds(
							EmbeddedMessage.MessageEmbed("Not Yet impemented", "Hujove tuke treba da pravit vija"))
							.queue();
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

	private void connectTo(AudioChannel channel) {
		Guild guild = channel.getGuild();
		AudioManager audioManager = guild.getAudioManager();
		AudioHandler handler = new AudioHandler();
		audioManager.setSendingHandler(handler);
		audioManager.openAudioConnection(channel);
	}

	private void voiceDisconnect(AudioChannel channel) {
		Guild guild = channel.getGuild();
		AudioManager audioManager = guild.getAudioManager();
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
