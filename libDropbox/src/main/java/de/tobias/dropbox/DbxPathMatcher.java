package de.tobias.dropbox;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.regex.Pattern;

public class DbxPathMatcher implements PathMatcher {

	private Pattern pattern;

	DbxPathMatcher(String syntaxAndInput) {
		pattern = Pattern.compile(syntaxAndInput);
	}

	@Override
	public boolean matches(Path path) {
		String pathName = path.toString();
		return pattern.matcher(pathName).matches();
	}

}
