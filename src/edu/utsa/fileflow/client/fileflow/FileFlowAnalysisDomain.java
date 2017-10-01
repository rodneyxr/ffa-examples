package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.AnalysisDomain;

public class FileFlowAnalysisDomain extends AnalysisDomain<FileFlowAnalysisDomain> {

	SymbolTable table = new SymbolTable();
	FileStructure init = new FileStructure();
	FileStructure post = new FileStructure();

	@Override
	public FileFlowAnalysisDomain merge(FileFlowAnalysisDomain domain) {
		table = (SymbolTable) table.merge(domain.table);
		post = post.merge(domain.post);
		init = init.merge(domain.init);
		return this;
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
		if (!table.equals(o.table))
			return 1;
		if (!post.equals(o.post))
			return 1;
		return 0;
	}

	@Override
	public FileFlowAnalysisDomain clone() {
		FileFlowAnalysisDomain clone = new FileFlowAnalysisDomain();
		clone.post = post.clone();
		clone.init = init.clone();
		table.forEach((k, v) -> clone.table.put(k, v.clone()));
		return clone;
	}

}
