package com.tempodbot.utils;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class RadioDecoder {
	
	
	final String radioURL;
	public RadioDecoder(String url){
		this.radioURL = url;
	}
	
	public AudioInputStream getRadioStream() {
		AudioInputStream mp3In = null;
		 AudioFormat pcmFormat = null;
		try {
			mp3In = AudioSystem.getAudioInputStream(new URL(radioURL));
	
        // AudioFormat describing the compressed stream
        final AudioFormat mp3Format = mp3In.getFormat();
        // AudioFormat describing the desired decompressed stream 
       pcmFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            mp3Format.getSampleRate(),
            16,
            mp3Format.getChannels(),
            16 * mp3Format.getChannels() / 8,
            mp3Format.getSampleRate(),
            mp3Format.isBigEndian()
            );
        // actually decompressed stream (signed PCM)
    //  // final AudioInputStream pcmIn = AudioSystem.getAudioInputStream(pcmFormat,mp3In);
		} catch (UnsupportedAudioFileException | IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
       return  AudioSystem.getAudioInputStream(pcmFormat,mp3In);
	}

}
