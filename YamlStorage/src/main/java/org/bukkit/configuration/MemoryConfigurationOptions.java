//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.bukkit.configuration;

public class MemoryConfigurationOptions extends ConfigurationOptions {
	protected MemoryConfigurationOptions(MemoryConfiguration configuration) {
		super(configuration);
	}

	public MemoryConfiguration configuration() {
		return (MemoryConfiguration)super.configuration();
	}

	public MemoryConfigurationOptions copyDefaults(boolean value) {
		super.copyDefaults(value);
		return this;
	}

	public MemoryConfigurationOptions pathSeparator(char value) {
		super.pathSeparator(value);
		return this;
	}
}