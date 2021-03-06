package de.thecodelabs.utils.ui;

import javafx.scene.Parent;

public interface DetailItem<T> {

	Parent getParent();

	T get();

	void set(T value);
}
