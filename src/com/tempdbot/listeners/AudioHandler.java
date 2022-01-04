package com.tempdbot.listeners;

import java.nio.ByteBuffer;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.utils.EmbeddedMessage;
import com.tempodbot.utils.ObservableState;
import com.tempodbot.utils.YTHandler;
import com.tempodbot.utils.onStateChangeListener;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * 
 * @author spectral369 This class is implements audiosendhandler that WILL
 *         process the sound to discord
 * 
 * 
 *
 */

public class AudioHandler extends  AudioEventAdapter implements AudioSendHandler {

	private final AudioPlayer audioPlayer;
	private List<MediaItem> queue;

	private AudioFrame lastFrame;
	private AudioPlayerManager playerManager;
	private YTHandler ythandler;
	private MessageChannel txtChannel;
	private AudioManager audiomanager;
	private ObservableState obsState;
	private AudioTrackState oldState = AudioTrackState.INACTIVE;

	public AudioHandler(Guild guild, List<MediaItem> queue, MessageChannel messageChannel) {
		this.queue = queue;
		this.txtChannel = messageChannel;
		playerManager = new DefaultAudioPlayerManager();
		audiomanager =  guild.getAudioManager();
		playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
		AudioSourceManagers.registerRemoteSources(playerManager);
		this.audioPlayer = playerManager.createPlayer();
		this.ythandler = new YTHandler(audioPlayer, messageChannel);
		audiomanager.setSendingHandler(this);
		audioPlayer.addListener(this);
		obsState =  new ObservableState();
	

	}

	public AudioPlayer getPlayer() {
		return audioPlayer;
		
	}

	public AudioTrack getTrack() {
		return audioPlayer.getPlayingTrack();
	}

	public boolean isMusicPlayering() {
		if (audioPlayer.getPlayingTrack().getState() == AudioTrackState.PLAYING)
			return true;
		else
			return false;
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {
		player.setPaused(true);
		txtChannel.sendMessageEmbeds(
				EmbeddedMessage.MessageEmbed("⏸️ " + audioPlayer.getPlayingTrack().getInfo().title + " paused", "")).queue();;
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		player.setPaused(false);
		txtChannel.sendMessageEmbeds(
				EmbeddedMessage.MessageEmbed("▶️ " + audioPlayer.getPlayingTrack().getInfo().title + " resumed", "")).queue();;
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReaso) {
		
		queue.remove(0);
		if (!queue.isEmpty()) {
			playerManager.loadItem(queue.get(0).url(), ythandler);
		}
		System.out.println("endTrACK");
		obsState.set(AudioTrackState.FINISHED);
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		txtChannel.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("▶️ Now Playing", track.getInfo().title)).queue();;
	}

	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
		if(audioPlayer.getPlayingTrack()!=null) {
			obsState.set(audioPlayer.getPlayingTrack().getState());
			obsState.setOnStateChangeListener(new onStateChangeListener() {
				
				@Override
				public void onStateChanged(AudioTrackState newValue) {
					if(oldState != newValue) {
					determineStatus(txtChannel, getTrack(),newValue);
					oldState=newValue;
					}
					
				}
			});
		}
		return lastFrame != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
	
		return ByteBuffer.wrap(lastFrame.getData());
	}

	@Override
	public boolean isOpus() {
		return true;
	}

	public void play() {
		if(audioPlayer.getPlayingTrack()!=null && audioPlayer.getPlayingTrack().getState() == AudioTrackState.PLAYING)
			return;
		else
		playerManager.loadItem(queue.get(0).url(), ythandler);
	}
	
	private void determineStatus(MessageChannel txtChannel2, AudioTrack track,AudioTrackState state) {
	
		if(state == AudioTrackState.PLAYING)
			txtChannel2.getJDA().getPresence().setActivity(Activity.listening(track.getInfo().title));
		if(state == AudioTrackState.FINISHED || state ==  AudioTrackState.INACTIVE)
			txtChannel2.getJDA().getPresence().setActivity(Activity.listening("No song in playing!"));
			
	}

}
