package de.thecodelabs.storage.serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Class for saving and reading serialized data
 *
 * @author Deadlocker8
 */
public class Serializer
{
	private Serializer()
	{
	}

	/**
	 * Saves the given byte[] in to a file
	 *
	 * @param path String - savepath
	 * @param data byte[] - data to save
	 * @throws Exception
	 */
	public static void serializeToFile(String path, byte[] data) throws Exception
	{
		try(BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path)))
		{
			stream.write(data);
		}
	}

	/**
	 * Reads a byte[] from a file
	 *
	 * @param path String - savepath
	 *             return  byte[] - data
	 * @throws Exception
	 */
	public static byte[] deserializeFromFile(String path) throws Exception
	{
		byte[] buffer;

		try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path)))
		{
			buffer = new byte[stream.available()];
			stream.read(buffer);
		}

		return buffer;
	}
}