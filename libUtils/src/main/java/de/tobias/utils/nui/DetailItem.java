package de.tobias.utils.nui;

import javafx.scene.Parent;

public interface DetailItem<T> {

	Parent getParent();

	T get();

	void set(T value);
}
