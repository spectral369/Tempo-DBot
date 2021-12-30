package com.tempdbot.listeners;

import java.nio.ByteBuffer;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.utils.EmbeddedMessage;
import com.tempodbot.utils.YTHandler;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
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
	private List<MediaItem> queue;

	private AudioFrame lastFrame;
	private AudioPlayerManager playerManager;
	private YTHandler ythandler;
	private TextChannel txtChannel;

	public AudioHandler(Guild guild, List<MediaItem> queue, TextChannel txtChannel) {
		this.queue = queue;
		this.txtChannel = txtChannel;
		playerManager = new DefaultAudioPlayerManager();
		playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
		// AudioSourceManagers.registerRemoteSources(playerManager);
		this.audioPlayer = playerManager.createPlayer();
		this.ythandler = new YTHandler(audioPlayer, txtChannel);

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
		if (!queue.isEmpty()) {
			playerManager.loadItem(queue.remove(0).url(), ythandler);

		}

	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		txtChannel.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("▶️ Now Playing", track.getInfo().title));

	}

	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
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
		playerManager.loadItem(queue.remove(0).url(), ythandler);
	}

}
