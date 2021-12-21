package com.tempdbot.listeners;

import java.nio.ByteBuffer;
import java.util.Queue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.utils.YTHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * 
 * @author spectral369 This class is implements audiosendhandler that WILL
 *         process the sound to discord
 * 
 * 
 *
 */

public class AudioHandler extends AudioEventAdapter implements AudioSendHandler {

	private final AudioPlayer audioPlayer;
	private Queue<String> queue;

	private AudioFrame lastFrame;
	AudioPlayerManager playerManager;
	private YTHandler ythandler;
	TextChannel txtChannel;

	public AudioHandler(Guild guild, Queue<String> queue, TextChannel txtChannel) {
		// this.guild = guild;
		this.txtChannel = txtChannel;
		this.queue = queue;

		// this.guildId = guild.getIdLong();
		playerManager = new DefaultAudioPlayerManager();
		playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
		// AudioSourceManagers.registerRemoteSources(playerManager);
		this.audioPlayer = playerManager.createPlayer();
		this.ythandler = new YTHandler(audioPlayer, txtChannel);

	}
	
	public AudioPlayer getPlayer() {
		return audioPlayer;
	}

	public int addTrackToFront() {
		// TODO to be implmented

		return -1;
	}

	public int addTrack(String url) {
		// TODO to be implemented
	//	playerManager.loadItem(queue., ythandler);
		return -1;
	}

	public MediaItem getTrack() {
		// TODO to be implemented
		return null;
	}

	public boolean isMusicPlayering() {
		// TODO to be implemented
		return false;
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {
		// Player was paused
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		// Player was resumed
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReaso) {
		// TODO to be implemented

	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		// TODO Nu-i bine aici, trebuie facuta clasa separata !

	}

	public Message getNowPlaying(JDA jda) {
		return null;
	}

	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
		return lastFrame != null;
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

	public void play() {
		playerManager.loadItem(queue.poll(), ythandler);

	}

}
