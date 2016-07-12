package de.tobias.utils.ui;

import javafx.stage.Window;

public interface Refreshable {

	public void updateData();

	public Window getStage();

	public default void refreshUI() {}
}