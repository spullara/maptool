package net.rptools.maptool.util;

import java.util.HashMap;
import java.util.Map;

public class CodeTimer {

	private Map<String, Timer> timeMap = new HashMap<String, Timer>();
	private String name;
	private long created = System.currentTimeMillis();
	
	public CodeTimer() {
		this("");
	}
	
	public CodeTimer(String name) {
		this.name = name;
	}
	
	public void start(String id) {
		
		Timer timer = timeMap.get(id);
		if (timer == null) {
			timer = new Timer();
			timeMap.put(id, timer);
		}
		
		timer.start();
	}
	
	public void stop(String id) {
		
		if (!timeMap.containsKey(id)) {
			throw new IllegalArgumentException("Could not find timer id: " + id);
		}
		
		timeMap.get(id).stop();
	}
	
	public long getElapsed(String id) {

		if (!timeMap.containsKey(id)) {
			throw new IllegalArgumentException("Could not find timer id: " + id);
		}
		
		return timeMap.get(id).getElapsed();
	}
	
	public void reset(String id) {
		timeMap.remove(id);
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Timer ").append(name).append(": ");
		builder.append(System.currentTimeMillis() - created).append("\n");
		for (Map.Entry<String, Timer> entry : timeMap.entrySet()) {
			builder.append("\t").append(entry.getKey()).append(": ").append(entry.getValue().getElapsed()).append("\n");
		}
		
		return builder.toString();
	}
	
	private static class Timer {
		
		long elapsed;
		long start = -1;
		
		public void start() {
			start = System.currentTimeMillis();
		}
		
		public void stop() {
			elapsed += (System.currentTimeMillis() - start);
			start = -1;
		}
		
		public long getElapsed() {
			long time = elapsed;
			if (start > 0) {
				time += (System.currentTimeMillis() - start);
			}
			return time;
		}
	}
}
