package de.thecodelabs.storage.serializer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Example
{
	private static final File SAVE_PATH = new File("data.dat");

	public static void main(String[] args)
	{
		String text = "Lorem Ipsum";
		String number = "5";
		String text2 = "AABBCCDDEE";

		Header header = new Header(1);
		header.addElement(text);
		header.addElement(number);
		header.addElement(text2);

		System.out.println("Header: " + header.createHeader());

		String completeText = text + number + text2;
		completeText = header.createHeader() + completeText;

		System.out.println("Complete Text: " + completeText);

		try
		{
			byte[] b = completeText.getBytes(StandardCharsets.UTF_8);
			System.out.println("bytes: " + Arrays.toString(b));

			Serializer.serializeToFile(SAVE_PATH.getAbsolutePath(), b);

			//Deserialize
			String data = new String(Serializer.deserializeFromFile(SAVE_PATH.getAbsolutePath()));
			header = new Header(data);

			ArrayList<String> parts = new ArrayList<>();
			int position = header.getHeaderSize();

			for(int i = 0; i < header.getNumberOfElements(); i++)
			{
				int currentSize = header.getHeaderPart(i);
				parts.add(data.substring(position, position + currentSize));
				position =  position + currentSize;
			}

			System.out.println(parts);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
