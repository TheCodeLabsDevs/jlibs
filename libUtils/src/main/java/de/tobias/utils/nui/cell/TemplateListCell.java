package de.tobias.utils.nui.cell;

import de.tobias.utils.nui.DetailItem;
import javafx.scene.control.ListCell;

public class TemplateListCell<T> extends ListCell<T> {

	private DetailItem<T> viewController;

	public TemplateListCell(DetailItem<T> viewController) {
		this.viewController = viewController;
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty) {
			viewController.set(item);
			setGraphic(viewController.getParent());
		} else {
			setGraphic(null);
		}
	}
}
