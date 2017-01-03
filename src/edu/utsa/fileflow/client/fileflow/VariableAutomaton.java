/**
 * This class simply wraps an Automaton object formatted as a variable.
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.Mergeable;

public class VariableAutomaton implements Mergeable<VariableAutomaton> {

	private Automaton variable;

	public VariableAutomaton(String fp) {
		boolean startsWithSlash = (fp.startsWith("/") || fp.startsWith("\\"));
		boolean endsWithSlash = (fp.endsWith("/") || fp.endsWith("\\"));
		StringBuilder sb = new StringBuilder();

		fp = FileStructure.clean(fp);
		String[] l = fp.split("/");

		// add a single initial slash if fp starts with slash
		if (startsWithSlash) {
			variable = Automaton.makeChar('/');
		} else {
			variable = Automaton.makeEmpty();
		}

		// build the automaton
		for (int i = 0; i < l.length; i++) {
			sb.append(l[i]);
			if (i != l.length - 1)
				sb.append('/');
			else if (endsWithSlash)
				sb.append('/');
			variable = variable.union(Automaton.makeString(sb.toString()));
		}
	}

	private VariableAutomaton(Automaton variable) {
		this.variable = variable;
	}

	public static VariableAutomaton bottom() {
		return new VariableAutomaton("");
	}

	public static VariableAutomaton top() {
		return new VariableAutomaton(Automaton.makeAnyString());
	}

	// Joining two automatons should ensure that there is only one slash between
	// the join
	// For example: 'dir1/' concat '/file1' should be 'dir1/file1' instead of
	// 'dir1//file1'
	public VariableAutomaton concat(VariableAutomaton v) {
		Automaton a = variable.concatenate(v.variable);
		// Automaton a1 = variable;
		// Automaton a2 = v.variable;
		// if (endsWith(FileStructure.separator()) &&
		// v.startsWith(FileStructure.separator()))
		// a = variable.concatenate(v.variable);
		return new VariableAutomaton(a);
	}

	public VariableAutomaton union(VariableAutomaton v) {
		Automaton a = variable.union(v.variable);
		return new VariableAutomaton(a);
	}

	public VariableAutomaton intersect(VariableAutomaton v) {
		Automaton a = variable.intersection(v.variable);
		return new VariableAutomaton(a);
	}

	public boolean endsWith(Automaton a) {
		Automaton result = variable.intersection(Automaton.makeAnyString().concatenate(a));
		return !result.isEmpty();
	}

	public boolean startsWith(Automaton a) {
		Automaton result = variable.intersection(a.concatenate(Automaton.makeAnyString()));
		return !result.isEmpty();
	}

	protected Automaton getAutomaton() {
		// TODO: make all '/' accept states and remove duplicates
		return variable;
	}

	@Override
	public VariableAutomaton merge(VariableAutomaton other) {
		return union(other);
	}

	@Override
	public VariableAutomaton clone() {
		return new VariableAutomaton(variable.clone());
	}

}
