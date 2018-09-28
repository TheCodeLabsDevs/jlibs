package de.tobias.utils.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

public class StringUtils {

	/**
	 * Erstellt einen Hashwert von einem String mit der SHA-256 Methode
	 * 
	 * @param string
	 *            Zu verarbeitender String
	 * @return Hashwert als String
	 */
	public static String sha256(String string) {
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-256");
			return hash(crypt, string);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("SHA-256 is not supported: " + e.getLocalizedMessage());
		}
		return string;
	}

	/**
	 * Erstellt einen Hashwert von einem String mit der SHA-1 Methode
	 * 
	 * @param string
	 *            Zu verarbeitender String
	 * @return Hashwert als String
	 */
	public static String sha1(String string) {
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			return hash(crypt, string);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("SHA-256 is not supported: " + e.getLocalizedMessage());
		}
		return string;
	}

	/**
	 * Hasht einen String mit einer bestimmten Methode
	 * 
	 * @param string
	 *            Zu verarbeitender String
	 * @param crypt
	 *            Hashverfahren
	 * @return Hashwert als String
	 */
	private static String hash(MessageDigest crypt, String string) {
		byte[] hash = crypt.digest(string.getBytes(StandardCharsets.UTF_8));
		return new String(hash);
	}

	/**
	 * Mehrere Strings werden zu einem zusammengefügt.
	 * 
	 * @param args
	 *            Einzelne Strings
	 * @return Zusammengefügter String
	 */
	public static String build(String[] args) {
		return build(args, " ");
	}

	public static String build(String[] args, String separator) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			builder.append(args[i]);
			if (i + 1 < args.length) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	/**
	 * Mehrere Strings werden zu einem zusammengefügt.
	 * 
	 * @param args
	 *            Einzelne Strings
	 * @return Zusammengefügter String
	 */
	public static String build(List<String> args) {
		return build(args, " ");
	}

	public static String build(List<String> args, String separator) {
		String[] argsArray = args.toArray(new String[0]);
		return build(argsArray, separator);
	}

	public static String concat(Iterable<?> content, String separator) {
		return concat(content.iterator(), separator);
	}


	public static String concat(Iterator<?> iterator, String sep) {
		StringBuilder builder = new StringBuilder();
		while (iterator.hasNext()) {
			builder.append(iterator.next());
			if (iterator.hasNext()) {
				builder.append(sep);
			}
		}
		return builder.toString();
	}

	@Deprecated
	public static String conact(Iterable<?> content, String separator) {
		return conact(content.iterator(), separator);
	}

	@Deprecated
	public static String conact(Iterator<?> iterator, String sep) {
		return concat(iterator, sep);
	}

	public static boolean isStringNotVisable(String string) {
		if (string.isEmpty()) {
			return true;
		} else if (string.matches("\\s")) {
			return true;
		} else if (string.equals("\n")) {
			return true;
		} else {
			return false;
		}
	}
}
