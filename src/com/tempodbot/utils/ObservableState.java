package com.tempodbot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

public class ObservableState
{
    private onStateChangeListener listener;

    private AudioTrackState value;

    public void setOnStateChangeListener(onStateChangeListener listener)
    {
        this.listener = listener;
    }

    public AudioTrackState get()
    {
        return value;
    }

    public void set(AudioTrackState value)
    {
        this.value = value;

        if(listener != null)
        {
        	listener.onStateChanged(value);
        }
    }
}