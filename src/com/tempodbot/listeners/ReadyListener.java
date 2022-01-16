package com.tempodbot.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class ReadyListener implements EventListener {

	@Override
	public void onEvent(GenericEvent event) {
		
		System.out.println("Bot ready !");
		System.out.println(event.getJDA().getGuilds());
		System.out.println(event.getJDA().getVoiceChannels());
		
	}


}
