package edu.utsa.fileflow.client.fileflow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import dk.brics.automaton.Automaton;

public class FFADomainTest {

	@Test
	public void testAutomatonCompare() throws Exception {
		Automaton auto1 = Automaton.makeString("/file1");
		Automaton auto2 = auto1.clone();

		// assert automaton clone is equal
		assertThat(auto1, is(auto2));

		// add a file to the auto2 and check that they are no longer equal
		auto2 = auto1.union(Automaton.makeString("/file2"));
		assertThat(auto1, not(auto2));
	}

}
