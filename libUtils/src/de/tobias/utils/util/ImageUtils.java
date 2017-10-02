package de.tobias.utils.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageUtils {

	public static ImageView getImageView(URL path) {
		ImageView view = new ImageView(new Image(path.toString()));
		return view;
	}

	public static ImageView getImageView(String path) {
		ImageView view = new ImageView(new Image(path));
		return view;
	}

	public static byte[] imageToByteArray(Image image) throws IOException {
		ByteArrayOutputStream oStr = new ByteArrayOutputStream();
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", oStr);
		return oStr.toByteArray();
	}

	public static Image byteArrayToImage(byte[] b) {
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		return new Image(in);
	}
	
	public static Image getImage(String path) throws IOException {
		return new Image(Files.newInputStream(Paths.get(path), StandardOpenOption.READ));
	}
}
