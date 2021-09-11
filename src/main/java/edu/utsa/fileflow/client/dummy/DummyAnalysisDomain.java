package edu.utsa.fileflow.client.dummy;

import edu.utsa.fileflow.analysis.AnalysisDomain;

public class DummyAnalysisDomain extends AnalysisDomain<DummyAnalysisDomain> {

	private static final DummyAnalysisDomain TOP = new DummyAnalysisDomain();
	private static final DummyAnalysisDomain BOTTOM = new DummyAnalysisDomain();
	static {
		TOP.flag = 1;
		BOTTOM.flag = 0;
	}

	int flag = 0;

	@Override
	public DummyAnalysisDomain merge(DummyAnalysisDomain domain) {
		if (flag != domain.flag) {
			flag = 1;
		}
		return this;
	}

	@Override
	public DummyAnalysisDomain top() {
		return TOP.clone();
	}

	@Override
	public DummyAnalysisDomain bottom() {
		return BOTTOM.clone();
	}

	@Override
	public int compareTo(DummyAnalysisDomain o) {
		if (flag < o.flag)
			return -1;
		if (flag > o.flag)
			return 1;
		return 0;
	}

	@Override
	public DummyAnalysisDomain clone() {
		DummyAnalysisDomain domain = new DummyAnalysisDomain();
		domain.flag = flag;
		return domain;
	}

	@Override
	public String toString() {
		return Integer.toString(flag);
	}

}
