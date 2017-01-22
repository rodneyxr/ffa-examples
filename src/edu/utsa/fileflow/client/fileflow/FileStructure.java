/**
 * This class represents a file structure using an automaton.
 * 
 * @author Rodney Rodriguez
 *
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import dk.brics.automaton.TransducerTransition;

public class FileStructure implements Cloneable {

	static {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	private static final Automaton SEPARATOR = Automaton.makeChar(VariableAutomaton.SEPARATOR);
	private static final Automaton ANY = Automaton.makeAnyString();

	private VariableAutomaton cwd = new VariableAutomaton("/");
	Automaton files;

	public FileStructure() {
		this.files = SEPARATOR.clone();
	}

	private FileStructure(Automaton files) {
		this.files = files;
	}

	public static FileStructure top() {
		Automaton files = SEPARATOR.clone();
		files.concatenate(Automaton.makeAnyString());
		return new FileStructure(files);
	}

	/**
	 * @return a clone of the separator automaton.
	 */
	public static Automaton separator() {
		return SEPARATOR.clone();
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
	 * Creates a file at the file path provided. Parent directory must exist for
	 * this operation to be successful. <code>fp</code> must not represent a
	 * directory (include a trailing slash).
	 * 
	 * @param fp
	 *            The file path to create the file.
	 * @throws FileStructureException
	 *             if the parent directory does not exist
	 */
	public void createFile(VariableAutomaton fp) throws FileStructureException {
		if (fp.isDirectory())
			throw new FileStructureException(String.format("touch: cannot touch '%s': Cannot touch a directory",
					fp.getAutomaton().getCommonPrefix()));

		// if the parent directory does not exist throw an exception
		if (!fileExists(fp.getParentDirectory()))
			throw new FileStructureException(String.format("touch: cannot touch '%s': No such file or directory",
					fp.getAutomaton().getCommonPrefix()));

		// if the file already exists, throw an exception
		if (fileExists(fp))
			throw new FileStructureException(String.format("touch: cannot touch '%s': File already exists",
					fp.getAutomaton().getCommonPrefix()));

		union(fp);
	}

	/**
	 * Creates a directory in the file structure at the path provided. If the
	 * path to that directory does not exist, it will be created.
	 * 
	 * @param fp
	 *            The file path to the directory to be created.
	 * @throws FileStructureException
	 *             if a file already exists at <code>fp</code>.
	 */
	public void createDirectory(VariableAutomaton fp) throws FileStructureException {
		// if fp does not have a trailing separator, then add one
		if (!fp.endsWith(SEPARATOR))
			fp = fp.concatenate(new VariableAutomaton("/"));

		VariableAutomaton a = new VariableAutomaton(absolute(fp));
		if (fileExists(a))
			throw new FileStructureException(String.format("mkdir: cannot create directory '%s': File exists",
					a.getAutomaton().getCommonPrefix()));

		union(fp);
	}

	/**
	 * Copies a file to another location in the file structure. Consider the
	 * following cases:
	 * <ul>
	 * <li/>
	 * <p/>
	 * file1 to file2: If file2 exists it will be overwritten; else it will be
	 * created
	 * <li/>
	 * <p/>
	 * file1 to dir2: file1 will be copied into dir2 overwriting file1 in dir2
	 * if it exists
	 * <li/>
	 * <p/>
	 * dir1 to dir2: a copy of dir1 will be created in dir2. if dir2/dir1
	 * happens to exist, contents will be merged overwriting the existing
	 * <li/>
	 * <p/>
	 * dir1 to file2: cp: cannot overwrite non-directory 'file2' with directory
	 * 'dir1'
	 * <li/>
	 * <p/>
	 * file1 to non-existing: cp: cannot create regular file
	 * 'non-existing/file1': No such file or directory
	 * <li/>
	 * <p/>
	 * dir1 to non-existing: cp: cannot create directory 'non-existing/dir1': No
	 * such file or directory
	 * </ul>
	 *
	 * @param source
	 *            The path pointing to the source file to copy
	 * @param destination
	 *            The path pointing to the destination location
	 * @throws FileStructureException
	 */
	public void copy(VariableAutomaton source, VariableAutomaton destination) throws FileStructureException {
		// if (dest exists && isdir) dest must end with a slash

		// check if source exists
		if (!fileExists(source))
			throw new FileStructureException(String.format("cp: cannot stat '%s': No such file or directory", source));

		// check if the paths point to the same file
		if (source.isSamePathAs(destination))
			throw new FileStructureException(String.format("cp: '%s' and '%s' are the same file", source, source));

		// make sure either the destination or parent to destination exists
		if (!fileExists(destination)) {
			if (!fileExists(destination.getParentDirectory())) {
				throw new FileStructureException(
						String.format("cp: cannot create file '%s': No such file or directory", destination));
			}
		}

		final Automaton src = absolute(source);
		final Automaton dst = absolute(destination);

		// get all files to be copied (absolute paths)
		Automaton a = files.intersection(src.concatenate(ANY));
		Automaton $src = Transducers.removeLastSeparator(src).concatenate(SEPARATOR);
		$src = $src.concatenate(ANY);

		// we need a FST to replace src prefix with empty
		FiniteStateTransducer replace = FiniteStateTransducer.AutomatonToTransducer($src);
		replace.getAcceptStates().forEach(s -> {
			s.getTransitions().forEach(t -> {
				((TransducerTransition) t).setIdentical(true);
			});
		});
		a = replace.intersection(a);
		// if source was a directory then initial state will be true
		// but we need it to be false
		a.getInitialState().setAccept(false);

		// after this we are left with everything after source in a
		// we need to prepend the base name to the result
		Automaton basename = Transducers.basename(src);
		a = basename.concatenate(SEPARATOR).concatenate(a);
		VariableAutomaton insert = new VariableAutomaton(dst.concatenate(a));

		// insert the files
		files = files.union(insert.getSeparatedAutomaton());
	}

	/**
	 * Determines whether a file exists. It does not matter if it is a directory
	 * or regular file or possibly both.
	 * 
	 * @param fp
	 *            The file path of the file to check if it exists.
	 * @return true if the file exists; false otherwise.
	 */
	public boolean fileExists(VariableAutomaton fp) {
		fp = new VariableAutomaton(absolute(fp));
		// try as a regular file
		fp = fp.removeLastSeparator();
		if (fp.subsetOf(files))
			return true;

		// try as a directory
		fp = fp.concatenate(new VariableAutomaton(SEPARATOR));
		return fp.subsetOf(files);
	}

	/**
	 * Tells if a file path is a directory in the file structure.
	 * 
	 * @param fp
	 *            The file path to check if a directory exists at.
	 * @return True if a directory exists at <code>fp</code>; false otherwise.
	 */
	public boolean isDirectory(VariableAutomaton fp) {
		fp = fp.concatenate(new VariableAutomaton(SEPARATOR));
		return fp.subsetOf(files);
	}

	/**
	 * Tells if a file path is a regular file in the file structure.
	 * 
	 * @param fp
	 *            The file path to check if a regular file exists at.
	 * @return True if a regular file exists at <code>fp</code>; false
	 *         otherwise.
	 */
	public boolean isRegularFile(VariableAutomaton fp) {
		fp = fp.removeLastSeparator();
		return fp.subsetOf(files);
	}

	/**
	 * Graphviz DOT representation of this file structure.
	 * 
	 * @return a Graphviz DOT representation of the files automaton.
	 */
	public String toDot() {
		return files.toDot();
	}

	/**
	 * Prepends the current working directory to the file path variable given.
	 * 
	 * @param fp
	 *            The file path to be appended to the current working directory.
	 * @return the absolute file path as an automaton.
	 */
	private Automaton absolute(VariableAutomaton fp) {
		if (fp.startsWith(cwd.getAutomaton())) {
			return fp.getAutomaton();
		}
		return cwd.concatenate(fp).getAutomaton();
	}

	/**
	 * Performs a union operation on <code>files</code>. <code>fp</code> is
	 * converted to an absolute path before the union.
	 * 
	 * @param fp
	 *            The variable automaton to union with <code>files</code>.
	 */
	private void union(VariableAutomaton fp) {
		files = files.union(new VariableAutomaton(absolute(fp)).getSeparatedAutomaton());
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

	@Override
	public FileStructure clone() {
		FileStructure clone = new FileStructure();
		clone.files = files.clone();
		return clone;
	}

}
