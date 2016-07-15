package edu.utsa.fileflow.client.postfix;

import java.util.HashMap;

import edu.utsa.fileflow.analysis.AnalysisDomain;

public class PostfixAnalysisDomain extends AnalysisDomain<PostfixAnalysisDomain> {

	private static final PostfixAnalysisDomain TOP = new PostfixAnalysisDomain();
	private static final PostfixAnalysisDomain BOTTOM = new PostfixAnalysisDomain();
	static {
		TOP.table = null;
		BOTTOM.table = new HashMap<>();
	}

	// map containing the variable/value pairs
	HashMap<String, PostfixItem> table = new HashMap<>();

	@Override
	public PostfixAnalysisDomain merge(PostfixAnalysisDomain other) {
		// x = 'abc'
		// y = 'ac'
		// prefix = 'a'
		// cut to common prefix
		// ab > abab*

		// if is bottom just return
		if (this.table.isEmpty()) {
			// this.table.putAll(other.table);
			// return this.clone();
			return this;
		}

		// add (merge) everything in other table to this table
		other.table.forEach((k, v2) -> {
			PostfixItem v1 = this.table.get(k);
			System.out.printf("v1: %s, v2: %s\n", v1, v2);

			// if item is only in other table
			if (v1 == null) {
				// just add it to this table
				System.out.printf("Adding '%s' to this table\n", v2);
				this.table.put(k, v2);
			} else {
				// item exists in both, so set this prefix to the LCP
				String lcp = PostfixItem.longestCommonPostfix(v1.postfix, v2.postfix);
				if (!lcp.equals(v1.postfix)) {
					v1.setPostfix(lcp);
					System.out.printf("(%s.java): ['%s' , '%s'] => '%s'\n", this.getClass().getSimpleName(), v1.postfix,
							v2.postfix, v1);
				}
			}
		});

		return this;
	}

	@Override
	public PostfixAnalysisDomain top() {
		return TOP.clone();
	}

	@Override
	public PostfixAnalysisDomain bottom() {
		return BOTTOM.clone();
	}

	@Override
	public int compareTo(PostfixAnalysisDomain other) {
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
			PostfixItem v1 = table.get(k);
			PostfixItem v2 = other.table.get(k);
			if (v2 == null)
				return 1;
			if (v1.postfix.length() < v2.postfix.length())
				return 1;
			if (v1.postfix.length() > v2.postfix.length())
				return -1;
			if (!v1.postfix.equals(v2.postfix))
				return -1;
		}
		return 0;
	}

	@Override
	public PostfixAnalysisDomain clone() {
		PostfixAnalysisDomain domain = new PostfixAnalysisDomain();
		domain.table.putAll(table);
		return domain;
	}

}
