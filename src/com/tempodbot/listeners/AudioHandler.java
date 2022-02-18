package com.tempodbot.listeners;

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
import com.tempodbot.handlers.YTHandler;
import com.tempodbot.interfaces.onStateChangeListener;
import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.mediaqueue.MediaItemType;
import com.tempodbot.statics.EmbeddedMessage;

import net.dv8tion.jda.api.EmbedBuilder;
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

public class AudioHandler extends AudioEventAdapter implements AudioSendHandler {

	private final AudioPlayer audioPlayer;
	private List<MediaItem> queue;

	private AudioFrame lastFrame;
	private AudioPlayerManager playerManager;
	private YTHandler ythandler;
	private MessageChannel txtChannel;
	private AudioManager audiomanager;
	private ObservableState obsState;
	private AudioTrackState oldState = AudioTrackState.INACTIVE;
	private boolean isRepeat = false;

	public AudioHandler(Guild guild, List<MediaItem> queue, MessageChannel messageChannel) {
		this.queue = queue;
		this.txtChannel = messageChannel;
		playerManager = new DefaultAudioPlayerManager();
		audiomanager = guild.getAudioManager();
		playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
		AudioSourceManagers.registerRemoteSources(playerManager);
		this.audioPlayer = playerManager.createPlayer();
		this.ythandler = new YTHandler(audioPlayer, messageChannel);
		audiomanager.setSendingHandler(this);
		audioPlayer.addListener(this);
		obsState = new ObservableState();

	}

	public void setRepeat(boolean repeat) {
		this.isRepeat = repeat;
	}

	public boolean getRepeat() {
		return this.isRepeat;
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
				EmbeddedMessage.MessageEmbed("⏸️ " + queue.get(0).name() + " paused", ""))
				.queue();
		;
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		player.setPaused(false);
		txtChannel.sendMessageEmbeds(
				EmbeddedMessage.MessageEmbed("▶️ " + queue.get(0).name() + " resumed", ""))
				.queue();
		;
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

		if (!isRepeat)
			queue.remove(0);

		if (!queue.isEmpty()) {
			playerManager.loadItem(queue.get(0).url(), ythandler);
		}
		System.out.println("endTrACK");
		obsState.set(AudioTrackState.FINISHED);
		txtChannel.sendMessageEmbeds(EmbeddedMessage.MessageEmbed("TrackEnd -> "+endReason.toString())).queue();
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		if(queue.get(0).type().equals(MediaItemType.RADIO))
			txtChannel.sendMessageEmbeds(new EmbedBuilder().setTitle(queue.get(0).name()).addField("Requestor", queue.get(0).requestor(), true).setThumbnail(queue.get(0).thumbnail()).build())
			.queue();
		else
		txtChannel.sendMessageEmbeds(EmbeddedMessage.MessageEmbed(track)).queue();
		
	}

	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
		if (audioPlayer.getPlayingTrack() != null) {
			obsState.set(audioPlayer.getPlayingTrack().getState());
			obsState.setOnStateChangeListener(new onStateChangeListener() {

				@Override
				public void onStateChanged(AudioTrackState newValue) {
					if (oldState != newValue) {
						determineStatus(txtChannel, getTrack(), newValue);
						oldState = newValue;
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
		if (audioPlayer.getPlayingTrack() != null
				&& audioPlayer.getPlayingTrack().getState() == AudioTrackState.PLAYING)
			return;
		else if (queue.get(0).type().equals(MediaItemType.YOUTUBE))
			playerManager.loadItem(queue.get(0).url(), ythandler);
		else if (queue.get(0).type().equals(MediaItemType.RADIO)) {
			playerManager.loadItem(queue.get(0).url(), ythandler);

		}
	}

	private void determineStatus(MessageChannel txtChannel2, AudioTrack track, AudioTrackState state) {

		if (state == AudioTrackState.PLAYING)
			txtChannel2.getJDA().getPresence().setActivity(Activity.listening(queue.get(0).name()));
		if (state == AudioTrackState.FINISHED || state == AudioTrackState.INACTIVE)
			txtChannel2.getJDA().getPresence().setActivity(Activity.listening("No song is playing!"));

	}

}
