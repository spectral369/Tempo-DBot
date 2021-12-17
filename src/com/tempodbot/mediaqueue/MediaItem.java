package com.tempodbot.mediaqueue;

public record MediaItem(String type, String url, String requestor, String name, String duration, boolean isLive,
		String description) {

}