/**
 * This class simply wraps an Automaton object formatted as a variable.
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.Mergeable;

public class VariableAutomaton implements Mergeable<VariableAutomaton> {

	public static final char SEPARATOR_CHAR = '/';
	public static final Automaton SEPARATOR_AUT = Automaton.makeChar(SEPARATOR_CHAR);
	public static final VariableAutomaton SEPARATOR_VA = new VariableAutomaton(SEPARATOR_AUT);
	
	private static final Automaton ANY_STRING_AUT = Automaton.makeAnyString();

	private Automaton variable;

	public VariableAutomaton(String fp) {
		this(Automaton.makeString(FileStructure.clean(fp)));
	}

	protected VariableAutomaton(Automaton variable) {
		// remove double separators if not bottom
		if (variable.isEmpty())
			this.variable = variable;
		else
			this.variable = Transducers.removeDoubleSeparators(variable);
	}

	public static VariableAutomaton bottom() {
		return new VariableAutomaton("");
	}

	public static VariableAutomaton top() {
		return new VariableAutomaton(SEPARATOR_AUT.concatenate(ANY_STRING_AUT));
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
	public VariableAutomaton concatenate(VariableAutomaton v) {
		Automaton a = variable.concatenate(v.variable);
		return new VariableAutomaton(a);
	}

	public VariableAutomaton union(VariableAutomaton v) {
		Automaton a = variable.union(v.variable);
		return new VariableAutomaton(a);
	}

	public VariableAutomaton intersection(VariableAutomaton v) {
		Automaton a = variable.intersection(v.variable);
		return new VariableAutomaton(a);
	}

	public boolean endsWith(Automaton a) {
		Automaton result = variable.intersection(ANY_STRING_AUT.concatenate(a));
		return !result.isEmpty();
	}

	public boolean startsWith(Automaton a) {
		Automaton result = variable.intersection(a.concatenate(ANY_STRING_AUT));
		return !result.isEmpty();
	}

	public boolean subsetOf(Automaton a) {
		return variable.subsetOf(a);
	}

	public boolean isSamePathAs(VariableAutomaton other) {
		VariableAutomaton a1 = removeLastSeparator();
		VariableAutomaton a2 = other.removeLastSeparator();
		return a1.variable.equals(a2.variable);
	}

	/**
	 * @return true if the automaton ends with a separator; false otherwise
	 */
	public boolean isDirectory() {
		return endsWith(SEPARATOR_AUT);
	}

	/**
	 * @return the parent directory of this automaton.
	 */
	public VariableAutomaton getParentDirectory() {
		VariableAutomaton a = SEPARATOR_VA.concatenate(this);
		return new VariableAutomaton(Transducers.parentDir(a.getAutomaton()));
	}

	/**
	 * 
	 * @return a clone of this automaton without the last separator if one
	 *         exists.
	 */
	public VariableAutomaton removeLastSeparator() {
		return new VariableAutomaton(Transducers.removeLastSeparator(variable));
	}

	public String toDot() {
		return variable.toDot();
	}

	/**
	 * @return the automaton without separators as accept states.
	 */
	protected Automaton getAutomaton() {
		return variable.clone();
	}

	/**
	 * Sets all separators to accept states.
	 * 
	 * @return the automaton with all separators as accept states.
	 */
	protected Automaton getSeparatedAutomaton() {
		Automaton a = variable.clone();

		// Make all separators accept states
		a.getStates().forEach(s -> {
			s.getTransitions().forEach(t -> {
				// if transition is a separator
				if (t.getMin() <= SEPARATOR_CHAR && t.getMax() >= SEPARATOR_CHAR) {
					// make destination state an accept state
					t.getDest().setAccept(true);
				}
			});
		});
		return a;
	}

	@Override
	public VariableAutomaton merge(VariableAutomaton other) {
		return union(other);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VariableAutomaton))
			return false;
		VariableAutomaton other = (VariableAutomaton) o;
		return variable.equals(other.variable);
	}

	@Override
	public String toString() {
		String singleton = variable.getSingleton();
		if (singleton != null)
			return singleton;
		return variable.getCommonPrefix();
	}

	@Override
	public VariableAutomaton clone() {
		return new VariableAutomaton(variable.clone());
	}

}
