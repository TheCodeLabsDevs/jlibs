package de.tobias.systemtray.test;

import de.tobias.systemtray.SystemTrayItem;
import de.tobias.systemtray.Utilities;
import de.tobias.systemtray.menu.Menu;
import de.tobias.systemtray.menu.MenuItem;
import de.tobias.systemtray.menu.SubMenu;
import de.tobias.systemtray.notification.Notification;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class TestSystemTray extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		SystemTrayItem item = new SystemTrayItem();
		item.setToolTip("Test");
		item.setImage(new Image("icon.png"));

		Menu menu = new Menu();
		SubMenu menu2 = new SubMenu("Dock Icon");
		MenuItem menuItem = new MenuItem("Hide Dock Icon");
		menuItem.setActionListener((i) -> Utilities.setDockIconHidden(true));

		MenuItem menuItem2 = new MenuItem("Show Dock Icon");
		menuItem2.setActionListener((i) -> Utilities.setDockIconHidden(false));

		MenuItem menuItem3 = new MenuItem("Notification");
		menuItem3.setActionListener((i) -> {
			try {
				Notification noti = new Notification("Backup Tool", "Löschen", "Löschen von alten Backups");
				noti.setListener((listener) -> {
					System.out.println("Test");
				});
				item.deliverNotification(noti);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		menu.addItem(menu2);
		menu2.addItem(menuItem);
		menu2.addItem(menuItem2);
		menu2.insertSeperator(1);
		menu.addItem(menuItem3);

		menu.addSeparator();

		MenuItem quit = new MenuItem("Test Programm Beenden");
		quit.setActionListener((i) -> Platform.exit());
		menu.addItem(quit);

		item.setMenu(menu);

		primaryStage.setScene(new Scene(new Group(new Label("rewbfhnfuhewaioufhdoüuisjfüoiwas"))));
//		primaryStage.show();
	}

	public static void main(String[] args) throws IOException {
		launch(args);
	}
}
