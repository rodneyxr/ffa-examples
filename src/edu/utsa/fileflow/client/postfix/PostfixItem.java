package edu.utsa.fileflow.client.postfix;

public class PostfixItem {

	// the end of the string
	String postfix;

	// true if anything before the postfix is unknown. ex: '*abc'
	private boolean unknown;

	public PostfixItem(String postfix) {
		this.postfix = postfix;
		this.unknown = false;
	}

	/**
	 * Sets this objects postfix and toggles unknown to true since changing the
	 * postfix implies that anything after postfix is unknown.
	 * 
	 * @param postfix
	 *            The new postfix to give to this object.
	 */
	public void setPostfix(String postfix) {
		this.postfix = postfix;
		unknown = true;
	}

	/**
	 * Concatenates the other PostfixItem to the end of the invoking
	 * PostfixItem. If other.unknown is true, the other's postfix becomes the
	 * new postfix.
	 * 
	 * @param other
	 *            The other PrefixItem to concat with this object.
	 * @return this object for chaining methods.
	 */
	public PostfixItem concat(PostfixItem other) {
		// 'a', 'b'
		// 'a', '*b'
		// '*a', 'b'
		// '*a', '*b'
		if (other.unknown) {
			// if other is unknown, then anything before does not matter
			unknown = true;
			postfix = other.postfix;
		} else {
			postfix += other.postfix;
		}
		return this;
	}

	/**
	 * 
	 * Calculates the longest common postfix given two String objects.
	 * 
	 * @param s1
	 *            The first String to check for the LCP.
	 * @param s2
	 *            The first String to check for the LCP.
	 * @return a String representing the longest common postfix (LCP).
	 */
	public static String longestCommonPostfix(String s1, String s2) {
		// reverse the strings and find longest common prefix
		// this still yields longest common postfix
		s1 = new StringBuilder(s1).reverse().toString();
		s2 = new StringBuilder(s2).reverse().toString();

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
	public boolean equals(Object other) {
		if (!(other instanceof PostfixItem))
			return false;
		PostfixItem o = (PostfixItem) other;
		return postfix.equals(o.postfix) && unknown == o.unknown;
	}

	@Override
	public String toString() {
		if (unknown)
			return "*" + postfix;
		return postfix;
	}

}
