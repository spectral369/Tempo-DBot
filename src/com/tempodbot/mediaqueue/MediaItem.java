package com.tempodbot.mediaqueue;

public record MediaItem(MediaItemType type, String url, String requestor, String name, String duration, boolean isLive,
		String description,
		String author,
		String thumbnail) {
}