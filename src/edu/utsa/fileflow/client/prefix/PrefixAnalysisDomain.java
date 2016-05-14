package edu.utsa.fileflow.client.prefix;

import java.util.HashMap;

import edu.utsa.fileflow.analysis.AnalysisDomain;

public class PrefixAnalysisDomain extends AnalysisDomain {

	private static final PrefixAnalysisDomain TOP = new PrefixAnalysisDomain();
	private static final PrefixAnalysisDomain BOTTOM = new PrefixAnalysisDomain();
	static {
		TOP.table = null;
		BOTTOM.table = new HashMap<>();
	}

	protected HashMap<String, String> table = new HashMap<>();

	@Override
	public PrefixAnalysisDomain merge(AnalysisDomain domain) {
		// TODO Auto-generated method stub
		return null;
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
	public int compareTo(AnalysisDomain o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PrefixAnalysisDomain clone() {
		PrefixAnalysisDomain domain = new PrefixAnalysisDomain();
		table.putAll(domain.table);
		return domain;
	}

}
