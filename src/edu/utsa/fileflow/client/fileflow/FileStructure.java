/**
 * This class represents a file structure using an automaton.
 * 
 * @author Rodney Rodriguez
 *
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;

public class FileStructure implements Cloneable {

	static {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	private static final Automaton SEPARATOR = Automaton.makeChar('/');
	private static final FiniteStateTransducer FST_PARENT = FiniteStateTransducer.parentDir();

	private VariableAutomaton cwd = new VariableAutomaton("/");
	Automaton files;

	public FileStructure() {
		this.files = SEPARATOR.clone();
	}

	public static FileStructure top() {
		Automaton files = SEPARATOR.clone();
		files.concatenate(Automaton.makeAnyString());
		return new FileStructure(files);
	}

	private FileStructure(Automaton files) {
		this.files = files;
	}

	/**
	 * @return a clone of the separator automaton.
	 */
	public static Automaton separator() {
		return SEPARATOR.clone();
	}

	/**
	 * Prepends the current working directory to the file path variable given.
	 * 
	 * @param fp
	 *            The file path to be appended to the current working directory.
	 * @return the absolute file path as an automaton.
	 */
	private Automaton makeAbsolute(VariableAutomaton fp) {
		return cwd.getAutomaton().concatenate(fp.getAutomaton());
	}

	/**
	 * Creates a file at the file path provided. Directory must exist for this
	 * operation to be successful.
	 * 
	 * @param fp
	 *            The file path to create the file.
	 * @return true if the operation was successful
	 */
	public boolean createFile(VariableAutomaton fp) {
		Automaton a = makeAbsolute(fp);
		Automaton parent = getParentDirectory(a);
		if (directoryExists(parent)) {
			files = files.union(a);
		} else {
			// parent does not exist here
			// TODO: decide whether to log this or stop execution
			System.out.println("touch: cannot touch: No such file or directory");
			return false;
		}
		return true;
	}

	public FileStructure createDirectory(Automaton fp) {
		Automaton a = fp.concatenate(SEPARATOR);
		if (!a.equals(fp))
			fp = a;
		files = files.union(fp);
		return this;
	}

	public boolean directoryExists(String fp) {
		fp = cleanDir(fp);
		return files.run(fp);
	}

	public boolean directoryExists(Automaton fp) {
		return fp.subsetOf(files);
	}

	public static Automaton getParentDirectory(Automaton a) {
		return FST_PARENT.intersection(a);
	}

	/**
	 * This is a special version of {@link Automaton#makeString}. It creates an
	 * automaton representation of a file that is compatible with and can be inserted
	 * into a {@link FileStructure}.
	 * 
	 * @param fp
	 *            The String representation of the file path.
	 * @return an automaton representation of <code>fp</code>.
	 */
	public static Automaton makeFileAutomaton(String fp) {
		return new VariableAutomaton("/" + fp).getAutomaton();
	}

	public static Automaton makeDirAutomaton(String fp) {
		fp = cleanDir(fp);
		return makeFileAutomaton(fp);
	}

	/**
	 * Cleans a string representing a file path.
	 * 
	 * @param fp
	 *            The file path to clean.
	 * @return a new String with the cleaned file path.
	 */
	public static String clean(String fp) {
		fp = fp.trim();
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
	public static String cleanDir(String fp) {
		clean(fp);
		if (!fp.endsWith("/"))
			fp = fp.concat("/");
		return fp;
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

}
