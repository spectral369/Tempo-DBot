package com.tempodbot.utils;

import java.util.TimerTask;

import net.dv8tion.jda.api.managers.AudioManager;

public class DisconnectTimerTask extends TimerTask {
	
	AudioManager voice;

	
	public DisconnectTimerTask(AudioManager audioManager) {
		this.voice =  audioManager;
	
	}

	@Override
	public void run() {
		if(voice.getGuild().getMembers().size()>1);
			voice.getGuild().getAudioManager().closeAudioConnection();
		
	}
}
