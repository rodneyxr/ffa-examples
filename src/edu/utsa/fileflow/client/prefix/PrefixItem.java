package edu.utsa.fileflow.client.prefix;

public class PrefixItem {

	// the beginning of the string
	String prefix;

	// the rest of the string after the prefix
	String tail;

	// true if tail can be any. ex: tail is *
	boolean hasAny;

	public PrefixItem(String prefix) {
		this.prefix = prefix;
		this.tail = "";
		this.hasAny = false;
	}

	public void prependPrefix(String prefix) {

	}

	public PrefixItem concat(PrefixItem other) {
		this.prefix = prefix + tail + other.prefix + other.tail;
		return this;
	}

	/**
	 * Calculates the longest common prefix given two String objects.
	 * 
	 * @param s1
	 *            The first String to check for the LCP.
	 * @param s2
	 *            The first String to check for the LCP.
	 * @return a String representing the longest common prefix (LCP).
	 */
	public static String longestCommonPrefix(String s1, String s2) {
		String min = s1;
		String max = s2;
		if (s2.length() < s1.length()) {
			min = s2;
			max = s1;
		}

		int end = min.length();
		for (int i = 0; i < end; i++) {
			if (min.charAt(i) != max.charAt(i)) {
				end = i;
				break;
			}
		}
		return min.substring(0, end);
	}

	@Override
	public String toString() {
		return prefix + tail;
	}

}
