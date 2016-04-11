package de.tobias.utils.help.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Tr;

import de.tobias.utils.help.HelpContent;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;
import de.tobias.utils.util.XMLUtils;

/**
 * Repräsentiert eine Tabelle
 * 
 * @author tobias
 *
 */
public class HelpTable extends HelpContent {

	/**
	 * Tabellenkopf (Tabellenspalten)
	 */
	private List<Column> columns;
	/**
	 * Objekte in der Tabelle
	 */
	private List<TableObject> objects;

	/**
	 * Erstellt ein neues Element
	 * 
	 * @param helpMap
	 *            Referenz zur HelpMap
	 */
	public HelpTable(HelpMap helpMap) {
		super(helpMap);
		columns = new ArrayList<>();
		objects = new ArrayList<>();
	}

	@Override
	public Node getNode(HelpMapViewController hmvc) {
		TableView<TableObject> table = new TableView<>();
		for (Column column : columns) {
			TableColumn<TableObject, String> tableColumn = new TableColumn<>(column.name);
			tableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().items.get(column.id)));
			table.getColumns().add(tableColumn);
		}
		table.getItems().addAll(objects);
		table.setMinHeight(150);
		table.setEditable(false);

		return table;
	}

	@Override
	public com.hp.gagawa.java.Node getHTMLNode() {
		Table table = new Table();
		table.setCSSClass("contentTable");
		Tr header = new Tr();
		columns.forEach(column -> {
			Td td = new Td();
			td.appendText(column.name);
			header.appendChild(td);
		});
		table.appendChild(header);

		objects.forEach(object -> {
			Tr row = new Tr();
			columns.forEach(column -> {
				Td td = new Td();
				td.appendText(object.items.get(column.id));
				row.appendChild(td);
			});
			table.appendChild(row);
		});

		return table;
	}

	@Override
	public HelpContent load(Element root) {
		XMLUtils.forEach(root.getElementsByTagName(HelpMap.columnItemTagName), node -> {
			Column column = new Column(node.getAttribute(HelpMap.idItemAttributeName), node.getTextContent());
			columns.add(column);
		});
		XMLUtils.forEach(root.getElementsByTagName(HelpMap.objectItemTagName), node -> {
			TableObject object = new TableObject();
			XMLUtils.forEach(node, entryNode -> {
				object.items.put(entryNode.getAttribute(HelpMap.idItemAttributeName), entryNode.getTextContent());
			});
			objects.add(object);
		});
		return this;
	}

	@Override
	public Element save(Element element, Document document) {
		return null;
	}

	/**
	 * Tabellenspalte
	 * 
	 * @author tobias
	 *
	 */
	private static class Column {
		/**
		 * UUID der Spalte
		 */
		private String id;
		/**
		 * Name / Titel der Spalte
		 */
		private String name;

		/**
		 * Erstellt eine neue Spalte
		 * 
		 * @param id
		 *            UUID
		 * @param name
		 *            Titel
		 */
		public Column(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	/**
	 * Tabellenobject
	 * 
	 * @author tobias
	 *
	 */
	private static class TableObject {
		/**
		 * Objekte der einzelnen Spalten
		 */
		private HashMap<String, String> items;

		/**
		 * Neues Tabellenobjekt
		 */
		public TableObject() {
			items = new HashMap<>();
		}
	}
}
