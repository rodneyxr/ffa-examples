package edu.utsa.fileflow.client.prefix;

import java.util.HashMap;

import edu.utsa.fileflow.analysis.AnalysisDomain;

public class PrefixAnalysisDomain extends AnalysisDomain<PrefixAnalysisDomain> {

	private static final PrefixAnalysisDomain TOP = new PrefixAnalysisDomain();
	private static final PrefixAnalysisDomain BOTTOM = new PrefixAnalysisDomain();
	static {
		TOP.table = null;
		BOTTOM.table = new HashMap<>();
	}

	// map containing the variable/value pairs
	HashMap<String, PrefixItem> table = new HashMap<>();

	@Override
	public PrefixAnalysisDomain merge(PrefixAnalysisDomain other) {
		// if is bottom just return
		if (this.table.isEmpty())
			return this;

		// add (merge) everything in other table to this table
		other.table.forEach((k, v2) -> {
			PrefixItem v1 = this.table.get(k);

			// if item is only in other table
			if (v1 == null) {
				// just add it to this table
				this.table.put(k, v2);
			} else {
				// item exists in both, so set this prefix to the LCP
				String lcp = PrefixItem.longestCommonPrefix(v1.prefix, v2.prefix);
				if (!lcp.equals(v1.prefix)) {
					v1.setPrefix(lcp);
				}
			}
		});

		return this;
	}

	@Override
	public PrefixAnalysisDomain top() {
		return TOP.clone();
	}

	@Override
	public PrefixAnalysisDomain bottom() {
		return BOTTOM.clone();
	}

	@Override
	public int compareTo(PrefixAnalysisDomain other) {
		// TODO: SMALLER | EQUAL | LARGER | UNDEFINED
		// FIXME: Not 100% correct but this method really just needs to check if
		// it is equal, which does work.
		if (table.size() < other.table.size())
			return -1;
		if (table.size() > other.table.size())
			return 1;

		// ab* is a superset of abab*
		// ab* > abab*
		for (String k : table.keySet()) {
			PrefixItem v1 = table.get(k);
			PrefixItem v2 = other.table.get(k);
			if (v2 == null)
				return 1;
			if (v1.prefix.length() < v2.prefix.length())
				return 1;
			if (v1.prefix.length() > v2.prefix.length())
				return -1;
			if (!v1.prefix.equals(v2.prefix))
				return -1;
		}
		return 0;
	}

	@Override
	public PrefixAnalysisDomain clone() {
		PrefixAnalysisDomain domain = new PrefixAnalysisDomain();
		domain.table.putAll(table);
		return domain;
	}

}
