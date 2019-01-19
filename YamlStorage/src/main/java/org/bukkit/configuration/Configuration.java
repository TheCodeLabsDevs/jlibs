//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.bukkit.configuration;

import java.util.Map;

public interface Configuration extends ConfigurationSection {
	void addDefault(String var1, Object var2);

	void addDefaults(Map<String, Object> var1);

	void addDefaults(Configuration var1);

	void setDefaults(Configuration var1);

	Configuration getDefaults();

	ConfigurationOptions options();
}
