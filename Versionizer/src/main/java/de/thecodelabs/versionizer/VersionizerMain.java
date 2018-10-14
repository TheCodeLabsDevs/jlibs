package de.thecodelabs.versionizer;

import com.google.gson.Gson;

public class VersionizerMain
{

	public static void main(String[] args)
	{
		Gson gson = new Gson();
		VersionizerItem[] parameters = gson.fromJson(args[0], VersionizerItem[].class);
	}
}
