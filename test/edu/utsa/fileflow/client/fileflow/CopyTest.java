package edu.utsa.fileflow.client.fileflow;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.TransducerTransition;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class CopyTest {

	FileStructure fs;
	final Automaton $x = new VariableAutomaton("/home/rodney/").getAutomaton();
	final Automaton $y = new VariableAutomaton("/dir1/dir2/").getAutomaton();

	@Before
	public void setUp() throws Exception {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
		fs = new FileStructure();
	}

	@Test
	public void testCopy() throws Exception {
		mkdir("/dir1/dir2/");
		mkdir("/root");
		mkdir("/home/rodney");
		touch("/home/rodney/bashrc");
		// cp '/home/rodney/' '/dir1/dir2/'

		Automaton a = fs.files;
		// get all files to be copied (absolute paths)
		// a = a intersect [$x concat *]
		a = a.intersection($x.concatenate(Automaton.makeAnyString()));

		Automaton $x_ = Transducers.removeLastSeparator($x).concatenate(Automaton.makeChar('/'));
		$x_ = $x_.concatenate(Automaton.makeAnyString());

		// need a replace FST to replace $x with empty
		FiniteStateTransducer replace = FiniteStateTransducer.AutomatonToTransducer($x_);
		replace.getAcceptStates().forEach(s -> {
			s.getTransitions().forEach(t -> {
				((TransducerTransition) t).setIdentical(true);
			});
		});

		a = replace.intersection(a);
		a.getInitialState().setAccept(false);
		Automaton basename = Transducers.basename($x);

		// after this we are left with everything after $x
		// we need to prepend the base name to the result
		a = basename.concatenate(Automaton.makeChar('/')).concatenate(a);
		VariableAutomaton insert = new VariableAutomaton($y.concatenate(a));

		// insert the files
		fs.files = fs.files.union(insert.getSeparatedAutomaton());

		save(fs.files, "test/copy/fs.dot");
		save(a, "test/copy/a.dot");
		save($x_, "test/copy/xprime.dot");
		save(replace, "test/copy/replace.dot");
		save(basename, "test/copy/basename.dot");
		save(insert.getAutomaton(), "test/copy/insert.dot");
	}

	void touch(String fp) throws FileStructureException {
		fs.createFile(regex(fp));
	}

	void mkdir(String fp) throws FileStructureException {
		fs.createDirectory(regex(fp));
	}

	// returns a variable automaton given a regex
	VariableAutomaton regex(String regex) {
		return new VariableAutomaton(new RegExp(regex).toAutomaton());
	}

	void save(Automaton a, String filepath) {
		GraphvizGenerator.saveDOTToFile(a.toDot(), filepath);
	}
}
