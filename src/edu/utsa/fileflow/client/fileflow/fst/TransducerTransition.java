package edu.utsa.fileflow.client.fileflow.fst;

import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

/**
 * A finite state transducer transition which belongs to a source state. Consists of a Unicode character interval and
 * output.
 * 
 * @author Rodney Rodriguez
 */
public class TransducerTransition extends Transition {
	private static final long serialVersionUID = 1L;

	// output when transition is made
	char output;

	// true if input should also serve as the output
	boolean isTransparent;

	public TransducerTransition(char c, State to) {
		this(c, c, to);
	}

	public TransducerTransition(char min, char max, State to) {
		this(min, max, '\u0000', to, true);
	}

	public TransducerTransition(char min, char max, char output, State to, boolean isTransparent) {
		super(min, max, to);
		this.output = output;
		this.isTransparent = isTransparent;
	}

	/**
	 * @return the output of this transition.
	 */
	public char getOutput() {
		return output;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TransducerTransition) {
			TransducerTransition t = (TransducerTransition) obj;
			return t.getMin() == getMin() && t.getMax() == getMax() && t.getDest() == getDest() && t.output == output;
		} else
			return false;
	}

	@Override
	public TransducerTransition clone() {
		TransducerTransition clone = (TransducerTransition) super.clone();
		clone.isTransparent = isTransparent;
		clone.output = output;
		return clone;
	}

}
