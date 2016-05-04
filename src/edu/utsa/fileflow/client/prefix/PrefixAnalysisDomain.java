package edu.utsa.fileflow.client.prefix;

import edu.utsa.fileflow.analysis.AnalysisDomain;

public class PrefixAnalysisDomain extends AnalysisDomain {

	private static final PrefixAnalysisDomain TOP = new PrefixAnalysisDomain();
	private static final PrefixAnalysisDomain BOTTOM = new PrefixAnalysisDomain();
	static {
		TOP.text = "*";
		BOTTOM.text = "";
	}

	String text;

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
		domain.text = text;
		return domain;
	}

}
