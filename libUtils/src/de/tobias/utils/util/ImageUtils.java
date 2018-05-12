package de.tobias.utils.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ImageUtils {

	public static ImageView getImageView(URL path) {
		return new ImageView(new Image(path.toString()));
	}

	public static ImageView getImageView(String path) {
		return new ImageView(new Image(path));
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
