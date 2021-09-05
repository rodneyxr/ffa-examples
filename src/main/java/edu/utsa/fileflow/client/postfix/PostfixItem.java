package edu.utsa.fileflow.client.postfix;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.client.prefix.PrefixItem;

/**
 * This class represents a postfix string. It also includes some other fields so
 * that we can tell if everything before the postfix is known or not. We can
 * also represent a bottom for postfix which is basically nothing (null).
 * 
 * @author Rodney Rodriguez
 *
 */
public class PostfixItem {

	// the end of the string
	private String postfix;

	// true if anything before the postfix is unknown. ex: '*abc'
	private boolean unknown;

	// true if prefix item is bottom (null)
	private boolean isBottom;

	public PostfixItem(String postfix) {
		this(postfix, false);
	}

	public PostfixItem(String postfix, boolean unknown) {
		this.postfix = postfix;
		this.unknown = unknown;
		this.isBottom = false;
	}

	/**
	 * @return the bottom representation of {@link PrefixItem}
	 */
	public static PostfixItem bottom() {
		PostfixItem bottom = new PostfixItem("");
		bottom.isBottom = true;
		return bottom;
	}

	/**
	 * Gets the postfix of this object.
	 * 
	 * @return the postfix represented by a {@link String}.
	 */
	public String getPostfix() {
		return postfix;
	}

	/**
	 * Sets this object's postfix and toggles unknown to true since changing the
	 * postfix implies that anything before postfix is unknown.
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
	 *            The other {@link PostfixItem} to concat with this object.
	 * @return this object for chaining methods.
	 */
	public PostfixItem concat(PostfixItem other) throws AnalysisException {
		if (isBottom || other.isBottom)
			throw new AnalysisException("Undefined PostfixItem; Cannot concat with BOTTOM.");
		if (other.unknown)
			return other.clone();
		if (unknown)
			return new PostfixItem(postfix + other.postfix, true);
		// not unknown
		return new PostfixItem(postfix + other.postfix, false);
	}

	/**
	 * Merges two PostfixItems. If one of the PostfixItems are bottom then the
	 * other one is returned.
	 * 
	 * @param other
	 *            The PostfixItem to be merged with the invoking PostfixItem.
	 * @return the merged PostfixItem.
	 */
	public PostfixItem merge(PostfixItem other) {
		if (isBottom)
			return other.clone();
		if (other.isBottom)
			return this;
		String lcp = PostfixItem.longestCommonPostfix(postfix, other.postfix);
		if (!lcp.equals(postfix)) {
			setPostfix(lcp);
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

		// since we reversed the sources, reverse the final answer
		return new StringBuilder(min.substring(0, end)).reverse().toString();
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

	@Override
	public PostfixItem clone() {
		if (isBottom)
			return PostfixItem.bottom();
		return new PostfixItem(postfix, unknown);
	}

}
