package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FFADomainTest {

    @Test
    public void testAutomatonCompare() {
        Automaton auto1 = Automaton.makeString("/file1");
        Automaton auto2 = auto1.clone();

        // assert automaton clone is equal
        assertEquals(auto1, auto2);

        // add a file to the auto2 and check that they are no longer equal
        auto2 = auto1.union(Automaton.makeString("/file2"));
        assertNotEquals(auto1, auto2);
    }

}
