/**
 * This class represents a file structure using two automatons. One of the automatons contains all directories and the
 * other contains the files.
 * 
 * @author Rodney Rodriguez
 *
 */
package edu.utsa.fileflow.client.fileflow;

import java.util.HashSet;
import java.util.Set;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

public class FileStructure implements Cloneable {

	static {
		Automaton.setMinimizeAlways(true);
	}

	private static final Automaton SLASH = Automaton.makeChar('/');

	Automaton files;

	public FileStructure() {
		this.files = SLASH.clone();
	}

	public static FileStructure top() {
		Automaton files = SLASH.clone();
		files.concatenate(Automaton.makeAnyString());
		return new FileStructure(files);
	}

	private FileStructure(Automaton files) {
		this.files = files;
	}

	/**
	 * Creates a file at the file path provided. Directory must exist for this operation to be successful.
	 * 
	 * @param fp
	 *            The file path to create the file.
	 * @return this FileStructure.
	 */
	public FileStructure createFile(Automaton fp) {
		files = files.union(fp);
		return this;
	}

	public FileStructure createDirectory(Automaton fp) {
		Automaton a = fp.concatenate(SLASH);
		if (!a.equals(fp))
			fp = a;
		files = files.union(fp);
		return this;
	}

	public boolean directoryExists(String fp) {
		fp = cleanDir(fp);
		return files.run(fp);
	}

	public static Automaton getPathToFile(Automaton a) {
		if (!a.isDeterministic())
			return null;

		// clone since we will be truncating the automaton
		a = a.clone();
		State s = a.getInitialState();
		Set<Transition> transitions;
		State last = s; // the last slash we have seen
		Set<State> statesToRemove = new HashSet<>();
		while (!(transitions = s.getTransitions()).isEmpty()) {
			State dest = null;

			for (Transition t : transitions) {
				// save the destination for the first transition
				if (dest == null)
					dest = t.getDest();
				// make sure all transitions go to the same state
				if (t.getDest() != dest) {
					return null;
				}

				// if t is a SLASH then remember this state
				if (t.getMin() <= '/' && t.getMax() >= '/') {
					last = s;
					statesToRemove.clear();
				}
			}
			statesToRemove.add(dest);
			s = dest;
		}

		// truncate the automaton from the last slash
		// a last slash is guaranteed because root is slash
		boolean skipFirst = true;
		for (State state : statesToRemove) {
			if (!skipFirst)
				state.setAccept(false);
			else
				skipFirst = false;
		}
		a.removeDeadTransitions();
		return a;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FileStructure))
			return false;
		FileStructure o = (FileStructure) obj;
		if (!files.equals(o.files))
			return false;
		return true;
	}

	/**
	 * Graphviz DOT representation of this file structure.
	 * 
	 * @return a Graphviz DOT representation of the files automaton.
	 */
	public String toDot() {
		return files.toDot();
	}

	@Override
	protected FileStructure clone() {
		FileStructure clone = new FileStructure();
		clone.files = files.clone();
		return clone;
	}

	/**
	 * Cleans a string representing a file path.
	 * 
	 * @param fp
	 *            The file path to clean.
	 * @return a new String with the cleaned file path.
	 */
	private String clean(String fp) {
		fp = fp.replaceAll("[/\\\\]+", "/");
		return fp;
	}

	/**
	 * Cleans a file path and appends a SLASH if fp does not end with one.
	 * 
	 * @param fp
	 *            The file path to clean.
	 * @return a new String with the cleaned directory file path.
	 */
	private String cleanDir(String fp) {
		clean(fp);
		if (!fp.endsWith("/"))
			fp = fp.concat("/");
		return fp;
	}

}
