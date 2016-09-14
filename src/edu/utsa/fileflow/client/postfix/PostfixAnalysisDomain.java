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
		// if is bottom just return
		if (this.table.isEmpty())
			return this;

		// add (merge) everything in other table to this table
		other.table.forEach((k, v2) -> {
			PostfixItem v1 = this.table.get(k);

			// if item is only in other table
			if (v1 == null) {
				// just add it to this table
				this.table.put(k, v2);
			} else {
				// item exists in both, so merge the two PostfixItems
				v1.merge(v2);
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

		for (String k : table.keySet()) {
			PostfixItem v1 = table.get(k);
			PostfixItem v2 = other.table.get(k);
			if (v2 == null)
				return 1;
			if (v1.getPostfix().length() < v2.getPostfix().length())
				return 1;
			if (v1.getPostfix().length() > v2.getPostfix().length())
				return -1;
			if (!v1.equals(v2))
				return -1;
		}
		return 0;
	}

	@Override
	public PostfixAnalysisDomain clone() {
		PostfixAnalysisDomain domain = new PostfixAnalysisDomain();
		// clone each value in this table over to the new table
		table.forEach((k, v) -> {
			domain.table.put(k, v.clone());
		});
		return domain;
	}

}
