package com.tempodbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

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
		txtChannel.getJDA().getPresence().setActivity(Activity.listening(track.getInfo().title));
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
	
		for (AudioTrack track : playlist.getTracks()) {
            audioPlayer.playTrack(track);
         }
		
	}

	@Override
	public void noMatches() {
		System.out.println("ce plm cauti");
		
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		System.out.println("o luat foc botu");
		
	}
	

}
