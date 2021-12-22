package com.tempodbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

public class YTHandler implements AudioLoadResultHandler{
	AudioPlayer audioPlayer;
	TextChannel txtChannel;
	
	public YTHandler(AudioPlayer player, TextChannel txtChannel) {
		this.audioPlayer = player;
		this.txtChannel =  txtChannel;
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		audioPlayer.playTrack(track);
		determineStatus(txtChannel, track);
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
	
	private void determineStatus(TextChannel txt, AudioTrack track) {
		System.out.println(track.getState());
		if(track.getState()  == AudioTrackState.PLAYING)
			txt.getJDA().getPresence().setActivity(Activity.listening(track.getInfo().title));
	}
	

}
