package de.tobias.dropbox;

import java.nio.file.attribute.FileAttributeView;

public class DbxFileAttributesView implements FileAttributeView {

	@Override
	public String name() {
		return "dbx";
	}

}
