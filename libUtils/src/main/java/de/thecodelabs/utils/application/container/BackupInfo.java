package de.thecodelabs.utils.application.container;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class BackupInfo implements ConfigurationSerializable {

	private String name;
	private long build;

	public BackupInfo(Map<String, Object> map) {
		name = (String) map.get("name");
		build = (int) map.get("build");
	}
	
	public BackupInfo(long time, long build) {
		this.name = Long.toString(time);
		this.build = build;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>(2);
		map.put("name", name);
		map.put("build", build);
		return map;
	}
}
