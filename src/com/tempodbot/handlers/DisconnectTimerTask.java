package com.tempodbot.handlers;

import java.util.TimerTask;

import com.tempodbot.listeners.AudioHandler;

import net.dv8tion.jda.api.managers.AudioManager;

public class DisconnectTimerTask extends TimerTask {
	
	AudioManager voice;
	AudioHandler handler;

	
	public DisconnectTimerTask(AudioManager audioManager, AudioHandler handler) {
		this.voice =  audioManager;
		this.handler = handler;
	}

	@Override
	public void run() {
		if(handler.getPlayer()!= null && handler.getPlayer().getPlayingTrack() != null)
			handler.getPlayer().destroy();
		
		if(voice.getConnectedChannel()!=null && voice.getConnectedChannel().getMembers().size()<=1) {
			voice.getGuild().getAudioManager().closeAudioConnection();
		}
	}
}
