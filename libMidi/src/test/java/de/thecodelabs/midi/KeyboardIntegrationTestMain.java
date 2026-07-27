package de.thecodelabs.midi;

import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.MappingRegistry;
import de.thecodelabs.midi.mapping.MappingSerializer;
import de.thecodelabs.midi.mapping.action.TestAction;
import de.thecodelabs.midi.mapping.action.TestActionHandler;
import de.thecodelabs.midi.mapping.input.KeyboardInputKey;
import de.thecodelabs.midi.mapping.listener.KeyboardMappingListener;
import de.thecodelabs.utils.io.IOUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class KeyboardIntegrationTestMain extends Application
{
	public static void main(String[] args)
	{
		launch(KeyboardIntegrationTestMain.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		final AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(new Label("Bla"));
		final Scene scene = new Scene(anchorPane);
		primaryStage.setScene(scene);
		primaryStage.setWidth(600);
		primaryStage.setHeight(400);
		primaryStage.show();

		final TestActionHandler actionHandler = new TestActionHandler();

		final MappingRegistry registry = new MappingRegistry();
		registry.registerAction(TestAction.class, actionHandler);
		registry.registerInputKey(KeyboardInputKey.class);
		final MappingSerializer serializer = registry.build();

		final Mapping mapping = serializer.deserialize(IOUtils.readURL(MidiIntegrationTestMain.class.getClassLoader().getResource("keyboard.json")));
		scene.addEventHandler(KeyEvent.ANY, new KeyboardMappingListener(mapping, registry));
	}
}
