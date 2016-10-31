package edu.utsa.fileflow.client.fileflow.fst;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;

public class FiniteStateTransducerTest {

		/**
		 * @throws java.lang.Exception
		 */
		@Before
		public void setUp() throws Exception {
			Automaton.setMinimizeAlways(true);
		}
		
		@Test
		public void testCreateFST() {
			FiniteStateTransducer fst = new FiniteStateTransducer();
		}

}
