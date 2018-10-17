package de.thecodelabs.utils.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class CollectionUtils {

	public static <T> T mostCommon(List<T> list) {
		Map<T, Integer> map = new HashMap<>();

		for (T t : list) {
			Integer val = map.get(t);
			map.put(t, val == null ? 1 : val + 1);
		}

		Entry<T, Integer> max = null;

		for (Entry<T, Integer> e : map.entrySet()) {
			if (max == null || e.getValue() > max.getValue())
				max = e;
		}

		return Objects.requireNonNull(max).getKey();
	}
}
