/**
 * This class simply wraps an Automaton object formatted as a variable.
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;

public class VariableAutomaton {

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

	protected Automaton getAutomaton() {
		return variable;
	}

}
