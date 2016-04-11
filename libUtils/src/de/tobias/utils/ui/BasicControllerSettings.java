package de.tobias.utils.ui;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;

import de.tobias.utils.settings.Required;
import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.Storable;

public class BasicControllerSettings implements SettingsSerializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@Storable @Required public double width = 600;
	/**
	 * 
	 */
	@Storable @Required public double height = 400;
	/**
	 * 
	 */
	@Storable @Required public boolean iconified = false;

	/**
	 * 
	 */
	@Storable protected MemorySection userInfo = new MemoryConfiguration();

	/**
	 * 
	 * @param key
	 * @param o
	 */
	public void addUserInfo(String key, Object o) {
		userInfo.set(key, o);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object getUserInfo(String key) {
		if (userInfo.contains(key)) {
			return userInfo.get(key);
		} else {
			return new String();
		}
	}

	public boolean getUserInfoAsBool(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getBoolean(key);
		} else {
			return false;
		}
	}

	public List<Boolean> getUserInfoAsBoolList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getBooleanList(key);
		} else {
			return null;
		}
	}

	public List<Byte> getUserInfoAsByteList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getByteList(key);
		} else {
			return null;
		}
	}

	public List<Character> getUserInfoAsCharList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getCharacterList(key);
		} else {
			return null;
		}
	}

	public double getUserInfoAsDouble(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getDouble(key);
		} else {
			return -1;
		}
	}

	public List<Double> getUserInfoAsDoubleList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getDoubleList(key);
		} else {
			return null;
		}
	}

	public List<Float> getUserInfoAsFloatList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getFloatList(key);
		} else {
			return null;
		}
	}

	public int getUserInfoAsInt(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getInt(key);
		} else {
			return -1;
		}
	}

	public List<Integer> getUserInfoAsIntList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getIntegerList(key);
		} else {
			return null;
		}
	}

	public List<?> getUserInfoAsList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getList(key);
		} else {
			return null;
		}
	}

	public long getUserInfoAsLong(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getLong(key);
		} else {
			return -1;
		}
	}

	public List<Long> getUserInfoAsLongList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getLongList(key);
		} else {
			return null;
		}
	}

	public List<Map<?, ?>> getUserInfoAsMapList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getMapList(key);
		} else {
			return null;
		}
	}

	public List<Short> getUserInfoAsShortList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getShortList(key);
		} else {
			return null;
		}
	}

	public String getUserInfoAsString(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getString(key);
		} else {
			return null;
		}
	}

	public List<String> getUserInfoAsStringList(String key) {
		if (userInfo.contains(key)) {
			return userInfo.getStringList(key);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param key
	 */
	public void removeUserInfo(String key) {
		userInfo.set(key, null);
	}
}
