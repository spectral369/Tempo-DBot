package com.thythmbot.listeners;

import java.nio.ByteBuffer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * 
 * @author spectral369
 * This class is implements audiosendhandler that WILL process the sound to discord
 * 
 * 
 *
 */

public class AudioHandler implements AudioSendHandler {
	 private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

	@Override
	public boolean canProvide() {
		// TODO Auto-generated method stub
		return queue.size() < 10;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		// TODO Auto-generated method stub
		 byte[] data = queue.poll();
         return data == null ? null : ByteBuffer.wrap(data);
	}

}
