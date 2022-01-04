package com.tempodbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.MessageChannel;

public class YTHandler implements AudioLoadResultHandler {
	AudioPlayer audioPlayer;
	MessageChannel txtChannel;
	
	public YTHandler(AudioPlayer player, MessageChannel messageChannel) {
		this.audioPlayer = player;
		this.txtChannel =  messageChannel;
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		audioPlayer.playTrack(track);
		//determineStatus(txtChannel, track);
		
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
	
		for (AudioTrack track : playlist.getTracks()) {
            audioPlayer.playTrack(track);
         }
		
	}

	@Override
	public void noMatches() {
		txtChannel.sendMessageEmbeds(
				EmbeddedMessage.MessageEmbed("Nope, no matches."))
				.queue();
		
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		txtChannel.sendMessageEmbeds(
				EmbeddedMessage.MessageEmbed("Error", "Hujove tuke treba da pravit vija\n "+exception))
				.queue();
		
	}
	
	public void stop() {
		audioPlayer.stopTrack();
	}
	
/*	private void determineStatus(MessageChannel txt, AudioTrack track) {
		System.out.println("In YTHANDLER: "+track.getState());
		if(track.getState()  == AudioTrackState.PLAYING)
			txt.getJDA().getPresence().setActivity(Activity.listening(track.getInfo().title));
	}*/
	

}