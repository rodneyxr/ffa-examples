package edu.utsa.fileflow.client.fileflow.fst;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import dk.brics.automaton.RegExp;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class FiniteStateTransducerTest {

	final Automaton VALID_CHARS = new RegExp("[a-zA-Z0-9.-_]{1}").toAutomaton();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Automaton.setMinimizeAlways(true);
		FiniteStateTransducer.setMinimizeAlways(true);
	}

	@Test
	public void testCreateFST() {
		FiniteStateTransducer fst = FiniteStateTransducer.parentDir();
		GraphvizGenerator.saveDOTToFile(fst.toDot(), "fst.dot");
	}

}
