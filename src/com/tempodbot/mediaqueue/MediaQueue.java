package com.tempodbot.mediaqueue;

import java.util.LinkedList;

public class MediaQueue extends LinkedList<MediaItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2688865693513037627L;
	
	@Override
	public MediaItem getFirst() {
		return this.getFirst();
	}
	
	@Override
	public MediaItem getLast() {
		return this.getLast();
	}
	
	public void enqueue(MediaItem item) {
		this.push(item);
	}
	
	public MediaItem dequeue(MediaItem item) {
		if(this.contains(item)) {
			return  this.poll();
		}else {
			return null;
		}
			
	}
	
	@Override
	public void clear() {
		this.clear();
	}
	
	public void shuffle() {
		this.shuffle();
	}
	
	
	public void move(int index1, int index2) {
		//TODO @Mike tuke treba ti da ij napravis !!!
		System.out.println("Not yet implemented !!!!!!");
	}
	
	

}





