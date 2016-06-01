package edu.utsa.fileflow.client.prefix;

public class PrefixItem {

	// the beginning of the string
	String prefix;

	// true if anything after the prefix is unknown. ex: 'abc*'
	private boolean unknown;

	public PrefixItem(String prefix) {
		this.prefix = prefix;
		this.unknown = false;
	}

	/**
	 * Concatenates the PrefixItem parameter to the end of the invoking
	 * PrefixItem. If this prefix is in the form of 'abc*', this method will
	 * have no effect since anything after 'abc' is unknown.
	 * 
	 * @param other
	 *            The other PrefixItem to concat to this object.
	 * @return this object for chaining methods.
	 */
	public PrefixItem concat(PrefixItem other) {
		if (!unknown) {
			this.prefix = prefix + other.prefix;
			if (other.unknown)
				this.unknown = true;
		}
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
		if (unknown)
			return prefix + "*";
		return prefix;
	}

}
