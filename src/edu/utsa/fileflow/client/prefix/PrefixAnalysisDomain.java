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
	public PrefixAnalysisDomain merge(PrefixAnalysisDomain domain) {
		// x = 'abc'
		// y = 'ac'
		// prefix = 'a'
		// cut to common prefix
		// ab > abab*

		HashMap<String, PrefixItem> t1 = new HashMap<>(domain.table);
		HashMap<String, PrefixItem> t2 = new HashMap<>(table);

		for (String k : t1.keySet()) {
			PrefixItem v1 = t1.get(k);
			PrefixItem v2 = t2.remove(k);

			// if variable only exists in v1 then prefix remains the same
			// but if it exists in v1 and v2 then cut to common prefix
			if (v2 != null) {
				// cut v1 and v2 to common prefix
				// TODO: implement this method
			}
		}

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
		table.putAll(domain.table);
		return domain;
	}

}
