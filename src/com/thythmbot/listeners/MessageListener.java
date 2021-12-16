package com.thythmbot.listeners;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * 
 * @author spectral369
 * This class handles messages from text channels and process them!
 *
 */

public class MessageListener implements EventListener {

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent messageEvent) {
			if (messageEvent.isFromType(ChannelType.PRIVATE)) {
				System.out.println("private message we should ignore this !");
				return;
			}
			if (messageEvent.isFromType(ChannelType.TEXT)) {
				
				
				User author = messageEvent.getAuthor();
				if(author.isBot()) {
					return;
				}
				if(!messageEvent.isFromGuild()) {
					return;
				}
				Message message = messageEvent.getMessage();
				MessageChannel channel = messageEvent.getChannel();
				String msg = message.getContentDisplay();
				Guild guild = messageEvent.getGuild(); // The Guild that this message was sent in. (note, in the API,
														// Guilds are Servers)
				TextChannel textChannel = messageEvent.getTextChannel(); // The TextChannel that this message was sent
																			// to.
				Member member = messageEvent.getMember(); // This Member that sent the message. Contains Guild specific
															// information about the User!

				String name;
				if (message.isWebhookMessage()) {
					name = author.getName(); // If this is a Webhook message, then there is no Member associated
				} // with the User, thus we default to the author for name.
				else {
					name = member.getEffectiveName(); // This will either use the Member's nickname if they have one,
				}
				 System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
		
				    if (msg.equals("!roll")) {
				    	Random rand = ThreadLocalRandom.current();
			            int roll = rand.nextInt(6) + 1; 
			            channel.sendMessage("Your roll: " + roll)
			                   .flatMap(
			                       (v) -> roll < 3, 
			                       sentMessage -> channel.sendMessage("The roll for messageId: " + sentMessage.getId() + " wasn't very good... Must be bad luck!\n")
			                   )
			                   .queue();
				    }else if(msg.trim().equals("!join")) {
				    	onConnecting(messageEvent, guild, member);
				    	connectTo(member.getVoiceState().getChannel());
				    }else if(msg.trim().equals("!leave")) {
				    	voiceDisconnect(member.getVoiceState().getChannel());
				    }
					
			}
		
			if (event instanceof SlashCommandEvent command) {
				System.out.println("slashcommand " + command.getName());
			}
			
		}
		

	}
	
	
	private void onConnecting(MessageReceivedEvent event,Guild guild,Member member) {
		AudioChannel channel = member.getVoiceState().getChannel();
		if(channel == null)
			return;
		else {
			event.getChannel().sendMessage("Connection"+channel.getName()).queue();
		}
		
			
	}
	
	private void connectTo(AudioChannel channel) {
		 Guild guild = channel.getGuild();
	     AudioManager audioManager = guild.getAudioManager();
	     AudioHandler handler =  new AudioHandler();
	     audioManager.setSendingHandler(handler);
	     audioManager.openAudioConnection(channel);
	}
	
	private void voiceDisconnect(AudioChannel channel){
		 Guild guild = channel.getGuild();
	     AudioManager audioManager = guild.getAudioManager();
	     audioManager.closeAudioConnection();
	}
	

}
