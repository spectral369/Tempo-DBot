package com.tempdbot.listeners;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.mediaqueue.MediaQueue;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;


/**
 * 
 * @author spectral369
 * This class is implements audiosendhandler that WILL process the sound to discord
 * 
 * 
 *
 */

public class AudioHandler extends AudioEventAdapter implements AudioSendHandler  {
	
	 private final MediaQueue queue =  new MediaQueue();
	 
	 
	 private final AudioPlayer audioPlayer;
	 private final Guild guild;
	 private final long guildId;
	 private AudioTrack currTrack;
	 
	 private AudioFrame lastFrame;
	 AudioPlayerManager playerManager;
	 
	 
	 public AudioHandler(Guild guild) {
		 this.guild = guild;
		 this.guildId = guild.getIdLong();
		 playerManager =   new DefaultAudioPlayerManager();
		 playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
		// AudioSourceManagers.registerRemoteSources(playerManager);
		 this.audioPlayer = playerManager.createPlayer();
		
	 }
	 
	 public  int addTrackToFront() {
		 //TODO to be implmented
		 return -1;
	 }
	 
	 public int addTrack(MediaItem track) {
		 //TODO to be implemented
		 
		 return -1;
	 }
	
	 
	 public MediaItem getTrack() {
		 //TODO to be implemented
		 return null;
	 }
	 
	 public boolean isMusicPlayering(){
		 //TODO to be implemented
		 return false;
	 }
	 
	 public void test() {
		 //TODO testing
		 
	 }
	 
	 
	 @Override
	 public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReaso) {
		 //TODO to be implemented
		 
	 }
	 
	 @Override
	 public void onTrackStart(AudioPlayer player, AudioTrack track) {
		 //TODO Nu-i bine aici, trebuie facuta clasa separata !
		 playerManager.loadItem("ytlink", new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(AudioTrack track) {
			
				player.playTrack(track);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
			
				 for (AudioTrack track : playlist.getTracks()) {
                     player.playTrack(track);
                 }
			}
			
			@Override
			public void noMatches() {
				// TODO tapa
				
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				// TODO O luat foc botu
				
			}
		});
	 }
	 
	 public Message getNowPlaying(JDA jda) {
		 return null;
	 }
	 

	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
		return lastFrame !=null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		// TODO Auto-generated method stub
		return ByteBuffer.wrap(lastFrame.getData());
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}

}
