package de.tobias.utils.util;

import java.util.Random;

public class RandomUtils {
	public enum RandomType {
		BASE_58,
		DEFAULT
	}

	public enum RandomStringPolicy {
		LOWER, UPPER, DIGIT
	}

	public static String generateRandomString(RandomType randomType, RandomStringPolicy... policies) {
		switch (randomType) {
			case BASE_58:
				return generateRandomBase58Char(policies);
			case DEFAULT:
				return generateRandomChar(policies);
			default:
				return null;
		}
	}

	public static String generateRandomString(RandomType randomType, int length, RandomStringPolicy... policies) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < length; i++) {
			result.append(generateRandomString(randomType, policies));
		}
		return result.toString();
	}

	private static String generateRandomChar(RandomStringPolicy... policies) {
		StringBuilder alphabet = new StringBuilder();

		for (RandomStringPolicy policy : policies) {
			switch (policy) {
				case LOWER:
					alphabet.append("abcdefghijklmnopqrstuvwxyz");
					break;
				case UPPER:
					alphabet.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
					break;
				case DIGIT:
					alphabet.append("123456789");
				default:
					break;
			}
		}

		int poolLength = alphabet.length();
		Random r = new Random();
		return String.valueOf(alphabet.charAt(r.nextInt(poolLength)));
	}

	/**
	 * generates a random BASE58 char
	 *
	 * @return String - random char
	 */
	private static String generateRandomBase58Char(RandomStringPolicy... policies) {
		StringBuilder alphabet = new StringBuilder();

		for (RandomStringPolicy policy : policies) {
			switch (policy) {
				case LOWER:
					alphabet.append("abcdefghijkmnopqrstuvwxyz");
					break;
				case UPPER:
					alphabet.append("ABCDEFGHJKLMNPQRSTUVWXYZ");
					break;
				case DIGIT:
					alphabet.append("123456789");
				default:
					break;
			}
		}

		int poolLength = alphabet.length();
		Random r = new Random();
		return String.valueOf(alphabet.charAt(r.nextInt(poolLength)));
	}


}
