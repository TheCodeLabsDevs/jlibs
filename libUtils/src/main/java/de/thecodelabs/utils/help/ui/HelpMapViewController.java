package de.thecodelabs.utils.help.ui;

import com.hp.gagawa.java.Document;
import com.hp.gagawa.java.DocumentType;
import com.hp.gagawa.java.elements.Style;
import de.thecodelabs.utils.help.HelpElement;
import de.thecodelabs.utils.help.HelpMap;
import de.thecodelabs.utils.io.FileUtils;
import de.thecodelabs.utils.io.IOUtils;
import de.thecodelabs.utils.ui.NVC;
import de.thecodelabs.utils.help.elements.HelpCategory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HelpMapViewController extends NVC implements ChangeListener<TreeItem<HelpElement>> {

	@FXML
	private TextField searchField;
	@FXML
	private Button backButton;
	@FXML
	private Button forthButton;

	@FXML
	private TreeView<HelpElement> treeView;
	@FXML
	private WebView webView;

	private HashMap<UUID, TreeItem<HelpElement>> treeItems;
	private List<HelpElement> searchResult;
	private boolean isSearching;

	private HelpElement currentElement;
	private Stack<HelpElement> backStack;
	private boolean isUndoing;
	private Stack<HelpElement> forthStack;
	private boolean isRedoing;

	private HelpMap helpMap;

	private GlyphFont font;
	private JSBridge bridge;

	public HelpMapViewController(HelpMap helpMap) {
		this(helpMap, "helpmap/view", "helpView");
	}

	public HelpMapViewController(HelpMap helpMap, String rootPath, String path) {
		load(rootPath, path);

		this.helpMap = helpMap;
		this.treeItems = new HashMap<>();
		this.searchResult = new ArrayList<>();
		this.isSearching = false;
		this.bridge = new JSBridge();

		TreeItem<HelpElement> root = new TreeItem<>();
		treeView.setShowRoot(false);
		treeView.setRoot(root);

		// Select Content
		treeView.getSelectionModel().selectedItemProperty().addListener(this);
	}

	public void loadItems() {
		treeView.getRoot().getChildren().clear();
		loadTree(treeView.getRoot(), helpMap.getElements());
	}

	private void loadTree(TreeItem<HelpElement> item, List<HelpElement> elements) {
		for (HelpElement element : elements) {
			if (element instanceof HelpCategory) {
				TreeItem<HelpElement> catItem = new TreeItem<HelpElement>(element);
				catItem.setGraphic(font.create(FontAwesome.Glyph.FOLDER_OPEN.name()));

				loadTree(catItem, ((HelpCategory) element).getChildElements());

				item.getChildren().add(catItem);
				treeItems.put(element.getUUID(), catItem);
			} else {
				TreeItem<HelpElement> topItem = new TreeItem<HelpElement>(element);
				topItem.setGraphic(font.create(FontAwesome.Glyph.FILE.name()));

				if (isSearching) {
					if (searchResult.contains(element))
						item.getChildren().add(topItem);
				} else
					item.getChildren().add(topItem);
				treeItems.put(element.getUUID(), topItem);
			}
		}
	}

	@Override
	public void init() {
		backStack = new Stack<>();
		forthStack = new Stack<>();
		isUndoing = false;

		font = new FontAwesome();

		backButton.setGraphic(font.create(FontAwesome.Glyph.ARROW_LEFT.name()));
		backButton.setText("");
		backButton.setDisable(true);
		forthButton.setGraphic(font.create(FontAwesome.Glyph.ARROW_RIGHT.name()));
		forthButton.setText("");
		forthButton.setDisable(true);
	}

	@Override
	public void initStage(Stage stage) {
		stage.setTitle("Hilfe");
		stage.getScene().getStylesheets().add("helpmap/style//style.css");
		stage.setMinWidth(800);
		stage.setMinHeight(500);
	}

	public void selectElement(HelpElement topic) {
		treeView.getSelectionModel().select(treeItems.get(topic.getUUID()));
	}

	@FXML
	private void searchHandler(ActionEvent e) {
		searchResult.clear();
		treeView.getRoot().getChildren().clear();

		if (!searchField.getText().isEmpty()) {
			searchResult.addAll(helpMap.search(searchField.getText()));
			isSearching = true;
		} else {
			isSearching = false;
		}

		loadTree(treeView.getRoot(), helpMap.getElements());
	}

	@FXML
	private void backButtonHandler(ActionEvent e) {
		isUndoing = true;
		HelpElement item = backStack.pop();
		forthStack.push(currentElement);
		selectElement(item);
		isUndoing = false;
	}

	@FXML
	private void forthButtonHandler(ActionEvent e) {
		isRedoing = true;
		HelpElement item = forthStack.pop();
		selectElement(item);
		isRedoing = false;
	}

	public void show() {
		loadItems();
		showStage();
	}

	private String getHtmlDocument(HelpElement helpElement) throws IOException {
		Path path = helpMap.getLocalResourcePath(helpElement.getUUID());

		if (Files.exists(path)) {
			return FileUtils.readFile(path);
		}

		Document document = new Document(DocumentType.HTMLStrict);

		// CSS
		Style style = new Style("text/css");
		style.appendText(IOUtils.readURL(getClass().getClassLoader().getResource("helpmap/style/helpmap.css")));
		document.head.appendChild(style);

		String content = helpElement.getHtmlDocument(this, document).write();

		Files.createDirectories(path.getParent());
		Files.write(path, content.getBytes());

		return content;
	}

	@Override
	public void changed(ObservableValue<? extends TreeItem<HelpElement>> observable, TreeItem<HelpElement> oldValue,
						TreeItem<HelpElement> newValue) {
		if (newValue != null) {
			try {
				if (oldValue != null && !isUndoing) {
					backStack.push(oldValue.getValue());
				}

				if (!isRedoing && !isUndoing) {
					forthStack.clear();
				}

				backButton.setDisable(backStack.isEmpty());
				forthButton.setDisable(forthStack.isEmpty());

				currentElement = newValue.getValue();

				String content = getHtmlDocument(currentElement);
				webView.getEngine().loadContent((content));
				webView.getEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
					if (newState == State.SUCCEEDED) {
						JSObject window = (JSObject) webView.getEngine().executeScript("window");
						window.setMember("app", bridge);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
	}

	public class JSBridge {

		public void select(String uuid) {
			selectElement(helpMap.findElement(UUID.fromString(uuid)));
		}
	}
}
