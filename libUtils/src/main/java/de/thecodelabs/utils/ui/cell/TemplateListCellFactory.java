package de.thecodelabs.utils.ui.cell;

import de.thecodelabs.utils.ui.DetailItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TemplateListCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {

	private Class<? extends DetailItem<T>> viewController;

	public TemplateListCellFactory(Class<? extends DetailItem<T>> viewController) {
		this.viewController = viewController;
	}

	@Override
	public ListCell<T> call(ListView<T> param) {
		try {
			return new TemplateListCell<>(viewController.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
