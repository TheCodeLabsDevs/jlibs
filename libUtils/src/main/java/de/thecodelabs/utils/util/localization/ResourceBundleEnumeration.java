package de.thecodelabs.utils.util.localization;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

// https://github.com/dueni/faces-ext/blob/master/resourcebundle/src/main/java/ch/dueni/util/ResourceBundleEnumeration.java
public class ResourceBundleEnumeration implements Enumeration<String>
{
	private Set<String> set;
	private Iterator<String> iterator;
	private Enumeration<String> enumeration;
	private String next = null;

	ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
		this.set = set;
		this.iterator = set.iterator();
		this.enumeration = enumeration;
	}


	public boolean hasMoreElements() {
		if (next == null) {
			if (iterator.hasNext()) {
				next = iterator.next();
			} else if (enumeration != null) {
				while (next == null && enumeration.hasMoreElements()) {
					next = enumeration.nextElement();
					if (set.contains(next)) {
						next = null;
					}
				}
			}
		}
		return next != null;
	}

	public String nextElement() {
		if (hasMoreElements()) {
			String result = next;
			next = null;
			return result;
		} else {
			throw new NoSuchElementException();
		}
	}
}
