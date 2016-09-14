package edu.utsa.fileflow.client.prefix;

import edu.utsa.fileflow.analysis.AnalysisException;

/**
 * This class represents a prefix string. It also includes some other fields so
 * that we can tell if everything after the prefix is known or not. We can also
 * represent a bottom for prefix which is basically nothing (null).
 * 
 * @author Rodney Rodriguez
 *
 */
public class PrefixItem implements Cloneable {

	// the beginning of the string
	private String prefix;

	// true if anything after the prefix is unknown. ex: 'abc*'
	private boolean unknown;

	// true if prefix item is bottom (null)
	private boolean isBottom;

	public PrefixItem(String prefix) {
		this(prefix, false);
	}

	public PrefixItem(String prefix, boolean unknown) {
		this.prefix = prefix;
		this.unknown = unknown;
		this.isBottom = false;
	}

	/**
	 * 
	 * @return the bottom representation of {@link PrefixItem}
	 */
	public static PrefixItem bottom() {
		PrefixItem bottom = new PrefixItem("");
		bottom.isBottom = true;
		return bottom;
	}

	/**
	 * Gets the prefix of this object.
	 * 
	 * @return the prefix represented by a {@link String}.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Sets this object's prefix and toggles unknown to true since changing the
	 * prefix implies that anything after prefix is unknown.
	 * 
	 * @param prefix
	 *            The new prefix to give to this object.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		unknown = true;
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
	public PrefixItem concat(PrefixItem other) throws AnalysisException {
		if (isBottom || other.isBottom)
			throw new AnalysisException("Undefined PrefixItem; Cannot concat with BOTTOM.");
		if (!unknown) {
			return new PrefixItem(prefix + other.prefix, other.unknown);
		} else {
			return clone();
		}
	}

	/**
	 * Merges two PrefixItems. If one of the PrefixItems are bottom then the
	 * other one is returned.
	 * 
	 * @param other
	 *            The PrefixItem to be merged with the invoking PrefixItem.
	 * @return the merged PrefixItem.
	 */
	public PrefixItem merge(PrefixItem other) {
		if (isBottom)
			return other.clone();
		if (other.isBottom)
			return this;
		String lcp = PrefixItem.longestCommonPrefix(prefix, other.prefix);
		if (!lcp.equals(prefix)) {
			setPrefix(lcp);
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
	public boolean equals(Object other) {
		if (!(other instanceof PrefixItem))
			return false;
		PrefixItem o = (PrefixItem) other;
		return prefix.equals(o.prefix) && unknown == o.unknown && isBottom == o.isBottom;
	}

	@Override
	public String toString() {
		if (isBottom)
			return "BOTTOM";
		if (unknown)
			return prefix + "*";
		return prefix;
	}

	@Override
	public PrefixItem clone() {
		if (isBottom)
			return PrefixItem.bottom();
		return new PrefixItem(prefix, unknown);
	}

}
