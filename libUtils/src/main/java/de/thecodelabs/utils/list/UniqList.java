package de.thecodelabs.utils.list;

import java.util.ArrayList;

public class UniqList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	public boolean add(T e) {
		if (!contains(e))
			return super.add(e);
		return false;
	}

}
