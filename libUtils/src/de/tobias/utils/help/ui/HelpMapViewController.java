package de.tobias.utils.help.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import de.tobias.utils.help.HelpElement;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.elements.HelpCategory;
import de.tobias.utils.help.elements.HelpTopic;
import de.tobias.utils.ui.ViewController;

public class HelpMapViewController extends ViewController {

	@FXML protected TextField searchField;
	@FXML protected TreeView<HelpElement> treeView;
	@FXML protected VBox contentView;
	@FXML public ScrollPane scrollPane;

	private HashMap<UUID, TreeItem<HelpElement>> items;
	private List<HelpElement> searchElements;
	private boolean isSearching;

	private HelpMap helpMap;

	public HelpMapViewController(HelpMap helpMap) {
		this(helpMap, "helpView", "de/tobias/utils/help/ui/assets");
		this.helpMap = helpMap;
	}

	public HelpMapViewController(HelpMap helpMap, String path, String rootPath) {
		super(path, rootPath);
		items = new HashMap<>();
		searchElements = new ArrayList<>();
		isSearching = false;

		TreeItem<HelpElement> root = new TreeItem<>();
		loadTree(root, helpMap.getElements());
		treeView.setShowRoot(false);
		treeView.setRoot(root);

		contentView.setFillWidth(true);

		// Select Content
		treeView.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
			if (c != null) {
				if (c.getValue() instanceof HelpTopic) {
					HelpTopic topicItem = (HelpTopic) c.getValue();
					contentView.getChildren().clear();

					Label headline = new Label(topicItem.getHeadline());
					headline.getStyleClass().add("headline");
					contentView.getChildren().add(headline);

					topicItem.getContents().forEach(contentItem -> {
						contentView.getChildren().add(contentItem.getNode(this));
					});
					contentView.getChildren().add(new Separator());
					String tags = "Schlagwörter: " + ((HelpTopic) topicItem).getTags();
					contentView.getChildren().add(getTextNode(tags));
				} else {
					c.setExpanded(true);
				}
			} else {
				contentView.getChildren().clear();
			}
		});
	}

	private void loadTree(TreeItem<HelpElement> item, List<HelpElement> elements) {
		for (HelpElement element : elements) {
			if (element instanceof HelpCategory) {
				TreeItem<HelpElement> catItem = new TreeItem<HelpElement>(element);
				catItem.setGraphic(new ImageView("de/tobias/utils/help/ui/assets/folder.png"));
				loadTree(catItem, ((HelpCategory) element).getChildElements());
				item.getChildren().add(catItem);
				items.put(element.getUUID(), catItem);
			} else {
				TreeItem<HelpElement> topItem = new TreeItem<HelpElement>(element);
				topItem.setGraphic(new ImageView("de/tobias/utils/help/ui/assets/file.png"));
				if (isSearching) {
					if (searchElements.contains(element)) {
						item.getChildren().add(topItem);
						item.setExpanded(true);
					}
				} else {
					item.getChildren().add(topItem);
					item.setExpanded(false);
				}
				items.put(element.getUUID(), topItem);
			}
		}
	}

	private Text getTextNode(String text) {
		Text textNode = new Text(text);
		textNode.wrappingWidthProperty().bind(getStage().widthProperty().subtract(235));
		textNode.setTextAlignment(TextAlignment.JUSTIFY);
		return textNode;
	}

	@Override
	public void init() {
		setCSS("style", "de/tobias/utils/help/ui/assets/");
	}

	@Override
	public void initStage(Stage stage) {
		stage.setTitle("Hilfe");
	}

	public void selectElement(HelpElement topic) {
		treeView.getSelectionModel().select(items.get(topic.getUUID()));
	}

	@FXML
	private void searchHandler(ActionEvent e) {
		treeView.getRoot().getChildren().clear();
		searchElements.clear();

		if (!searchField.getText().isEmpty()) {
			searchElements.addAll(helpMap.search(searchField.getText()));
			isSearching = true;
		} else {
			isSearching = false;
		}

		loadTree(treeView.getRoot(), helpMap.getElements());
	}
	
	public void show() {
		getStage().show();
	}
}
