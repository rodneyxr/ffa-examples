package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.AnalysisDomain;

public class FileFlowAnalysisDomain extends AnalysisDomain<FileFlowAnalysisDomain> {

	FileStructure post = new FileStructure();

	SymbolTable table = new SymbolTable();

	@Override
	public FileFlowAnalysisDomain merge(FileFlowAnalysisDomain domain) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("FileFlowAnalysisDomain.merge()");
	}

	@Override
	public FileFlowAnalysisDomain top() {
		FileFlowAnalysisDomain top = new FileFlowAnalysisDomain();
		top.post = FileStructure.top();
		return top;
	}

	@Override
	public FileFlowAnalysisDomain bottom() {
		FileFlowAnalysisDomain bottom = new FileFlowAnalysisDomain();
		bottom.post = new FileStructure();
		return bottom;
	}

	@Override
	public int compareTo(FileFlowAnalysisDomain o) {
		if (!post.equals(o.post))
			return 1;
		return 0;
	}

	@Override
	public FileFlowAnalysisDomain clone() {
		FileFlowAnalysisDomain clone = new FileFlowAnalysisDomain();
		clone.post = post.clone();
		return clone;
	}

}
