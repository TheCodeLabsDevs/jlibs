package de.thecodelabs.utils.ui.notification;

import de.thecodelabs.utils.threading.Worker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class NotificationExample extends Application
{
	private static NotificationElement lastElement;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		final Image defaultIcon = new Image("notification/info.png");
		final Image defaultIcon2 = new Image("notification/info2.png");

		Notification notification = new Notification(350, 120, 5, 50, 4000, 1000, stage, defaultIcon, null);

		Worker.runLater(() -> {
			for(int i = 0; i < 15; i++)
			{
				try
				{
					Thread.sleep(200);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				int index = i;
				Platform.runLater(() -> {
					Image icon = defaultIcon;
					if(index % 2 == 0)
					{
						icon = defaultIcon2;
					}
					lastElement = notification.show("Achievement unlocked", "Notification #" + index, icon);
				});
			}

			try
			{
				Thread.sleep(700);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			Platform.runLater(() -> notification.close(lastElement, true));
		});

		Worker.shutdown();
	}
}
