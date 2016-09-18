package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.AnalysisDomain;

public class FileFlowAnalysisDomain extends AnalysisDomain<FileFlowAnalysisDomain> {

	Automaton post = new Automaton();

	@Override
	public FileFlowAnalysisDomain merge(FileFlowAnalysisDomain domain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileFlowAnalysisDomain top() {
		FileFlowAnalysisDomain top = new FileFlowAnalysisDomain();
		return top;
	}

	@Override
	public FileFlowAnalysisDomain bottom() {
		FileFlowAnalysisDomain bottom = new FileFlowAnalysisDomain();
		bottom.post = Automaton.makeString("/");
		return bottom;
	}

	@Override
	public int compareTo(FileFlowAnalysisDomain o) {
		if (post.equals(o.post))
			return 0;
		return 1;
	}

	@Override
	public FileFlowAnalysisDomain clone() {
		FileFlowAnalysisDomain clone = new FileFlowAnalysisDomain();
		clone.post = post.clone();
		return clone;
	}

}
