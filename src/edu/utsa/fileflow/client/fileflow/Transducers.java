package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import dk.brics.automaton.TransducerState;
import dk.brics.automaton.TransducerTransition;

public class Transducers {

	private static final FiniteStateTransducer FST_BASENAME = basename();
	private static final FiniteStateTransducer FST_PARENT_DIR = parentDir();
	private static final FiniteStateTransducer FST_REMOVE_DOUBLE_SEP = removeDoubleSeparator();
	private static final FiniteStateTransducer FST_REMOVE_LAST_SEP = removeLastSeparator();

	public static Automaton basename(Automaton a) {
		Automaton result = FST_BASENAME.intersection(a);
		result.getAcceptStates().forEach(s -> {
			s.setAccept(s.getTransitions().isEmpty());
		});
		return result;
	}

	public static Automaton parentDir(Automaton a) {
		return FST_PARENT_DIR.intersection(a);
	}

	public static Automaton removeDoubleSeparators(Automaton a) {
		return FST_REMOVE_DOUBLE_SEP.intersection(a);
	}

	public static Automaton removeLastSeparator(Automaton a) {
		return FST_REMOVE_LAST_SEP.intersection(a);
	}

	static FiniteStateTransducer basename() {
		FiniteStateTransducer fst = new FiniteStateTransducer();
		TransducerState s0 = new TransducerState();
		TransducerState s1 = new TransducerState();
		TransducerState s2 = new TransducerState();

		// s0 -> s0: accept anything => epsilon
		s0.addEpsilonAcceptAllTransition(s0);

		// s0 -> s1: accept '/' => epsilon
		s0.addTransition(TransducerTransition.createEpsilonTransition('/', '/', s1));

		// s1 -> s1: accept anything minus '/' => identical
		s1.addIdenticalExcludeTransition('/', s1);

		// s1 -> s2: accept '/' => epsilon
		s1.addTransition(TransducerTransition.createEpsilonTransition('/', '/', s2));

		// s2 -> s2: accept '/' => epsilon
		s2.addTransition(TransducerTransition.createEpsilonTransition('/', '/', s2));

		s1.setAccept(true);
		s2.setAccept(true);
		fst.setInitialState(s0);
		return fst;
	}

	static FiniteStateTransducer parentDir() {
		// Forward Slash - '/' => '\u002f'
		// Back Slash '\' => '\u005c'
		FiniteStateTransducer fst = new FiniteStateTransducer();
		TransducerState s0 = new TransducerState();
		TransducerState s1 = new TransducerState();
		TransducerState s2 = new TransducerState();
		TransducerState s3 = new TransducerState();

		// s0 -> s0: accept anything
		s0.addIdenticalAcceptAllTransition(s0);

		// s0 -> s1: '/' => epsilon
		s0.addTransition(new TransducerTransition('/', '/', s1));

		// s1 -> s2: accept anything minus '/' => epsilon
		s1.addEpsilonExcludeTransition('/', s2);

		// s2 -> s2: accept anything minus '/' => epsilon
		s2.addEpsilonExcludeTransition('/', s2);

		// s2 -> s3: '/' => epsilon
		s2.addTransition(TransducerTransition.createEpsilonTransition('/', '/', s3));

		s2.setAccept(true);
		s3.setAccept(true);
		fst.setInitialState(s0);
		return fst;
	}

	// S0 -> S0: input=All-'/', output=identical
	// S0 -> S1: input='/', output = identical
	// S1 -> S2: input='/', output = empty
	// S1 -> S0: input = All-'/', output = identical
	// S2 -> S0: input = All-'/', output = identical
	static FiniteStateTransducer removeDoubleSeparator() {
		FiniteStateTransducer fst = new FiniteStateTransducer();
		TransducerState s0 = new TransducerState();
		TransducerState s1 = new TransducerState();
		TransducerState s2 = new TransducerState();

		// s0 -> s0: accept anything minus '/' => identical output
		s0.addIdenticalExcludeTransition('/', s0);

		// s0 -> s1: only accept '/' => identical output
		s0.addTransition(new TransducerTransition('/', s1));

		// s1 -> s2: only accept '/' => epsilon
		s1.addTransition(TransducerTransition.createEpsilonTransition('/', '/', s2));

		// s1 -> s0: accept anything minus '/' => identical output
		s1.addIdenticalExcludeTransition('/', s0);

		// s2 -> s0: accept anything minus '/' => identical output
		s2.addIdenticalExcludeTransition('/', s0);

		s0.setAccept(true);
		s1.setAccept(true);
		s2.setAccept(true);
		fst.setInitialState(s0);
		return fst;
	}

	static FiniteStateTransducer removeLastSeparator() {
		FiniteStateTransducer fst = new FiniteStateTransducer();
		TransducerState s0 = new TransducerState();
		TransducerState s1 = new TransducerState();
		TransducerState s2 = new TransducerState();

		// s0 -> s0: accept anything
		s0.addIdenticalAcceptAllTransition(s0);

		// s0 -> s1: accept anything minus '/' => identical
		s0.addIdenticalExcludeTransition('/', s1);

		// s1 -> s1: accept anything minus '/' => identical
		s1.addIdenticalExcludeTransition('/', s1);

		// s1 -> s2: '/' => epsilon
		s1.addTransition(TransducerTransition.createEpsilonTransition('/', '/', s2));

		s1.setAccept(true);
		s2.setAccept(true);
		fst.setInitialState(s0);
		return fst;
	}

}
