/**
 * This class simply wraps an Automaton object formatted as a variable.
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import edu.utsa.fileflow.analysis.Mergeable;

public class VariableAutomaton implements Mergeable<VariableAutomaton> {

	public static final char SEPARATOR = '/';
	private static final FiniteStateTransducer FST_REMOVE_DOUBLE_SEP = FiniteStateTransducer.removeDoubleSeparator();

	private Automaton variable;

	public VariableAutomaton(String fp) {
		fp = FileStructure.clean(fp);
		variable = Automaton.makeString(fp);
	}

	private VariableAutomaton(Automaton variable) {
		this.variable = variable;
	}

	public static VariableAutomaton bottom() {
		return new VariableAutomaton("");
	}

	public static VariableAutomaton top() {
		return new VariableAutomaton(Automaton.makeChar(SEPARATOR).concatenate(Automaton.makeAnyString()));
	}

	/**
	 * Joins two automatons to ensure that there is only one slash between the
	 * join For example: 'dir1/' + '/file1' should be 'dir1/file1' instead of
	 * 'dir1//file1'
	 * 
	 * @param v
	 *            The {@link VariableAutomaton} to append to this object.
	 * @return A new {@link VariableAutomaton} object with <code>v</code>
	 *         concatenated.
	 */
	public VariableAutomaton concat(VariableAutomaton v) {
		Automaton a = variable.concatenate(v.variable);
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

	public boolean isDirectory() {
		return endsWith(Automaton.makeChar(SEPARATOR));
	}

	@Override
	public VariableAutomaton merge(VariableAutomaton other) {
		return union(other);
	}

	protected Automaton getAutomaton() {
		// Remove double separators
		Automaton a = FST_REMOVE_DOUBLE_SEP.intersection(variable);

		// Make all separators accept states
		a.getStates().forEach(s -> {
			s.getTransitions().forEach(t -> {
				// if transition is a separator
				if (t.getMin() <= SEPARATOR && t.getMax() >= SEPARATOR) {
					// make destination state an accept state
					t.getDest().setAccept(true);
				}
			});
		});
		return a;
	}

	public String toDot() {
		return variable.toDot();
	}

	@Override
	public VariableAutomaton clone() {
		return new VariableAutomaton(variable.clone());
	}

}
