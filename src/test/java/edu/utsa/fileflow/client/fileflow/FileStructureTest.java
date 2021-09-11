package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import edu.utsa.fileflow.utilities.GraphvizGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class FileStructureTest {

    private FileStructure fs;

    @BeforeEach
    public void setUp() throws Exception {
        Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
        Automaton.setMinimizeAlways(true);
        fs = new FileStructure();
    }

    @Test
    public void testDirectoryExists() throws FileStructureException {
        fs.createDirectory(new VariableAutomaton("/home"));
        // '/' exists by default since it is root
        assertTrue(fs.fileExists(new VariableAutomaton("/")), "'/' should exist");
        assertTrue(fs.fileExists(new VariableAutomaton("/home")), "'/home' should exist");
        assertFalse(fs.fileExists(new VariableAutomaton("/fake")), "'/fake' should not exist");
    }

    @Test
    public void testFileExists() throws FileStructureException {
        fs.createDirectory(new VariableAutomaton("/home"));
        fs.createFile(new VariableAutomaton("/home/file"));

        // '/' exists by default since it is root
        assertTrue(fs.fileExists(new VariableAutomaton("/")), "'/' should exist");
        assertTrue(fs.fileExists(new VariableAutomaton("/home")), "'/home' should exist");
        assertTrue(fs.fileExists(new VariableAutomaton("/home/file")), "'/home/file' should exist");
        assertFalse(fs.fileExists(new VariableAutomaton("/fake")), "'/fake' should not exist");
    }

    @Test
    public void testCreateFileWhenDirectoryAlreadyExists() throws FileStructureException {
        // create and assert '/a/'
        fs.createDirectory(new VariableAutomaton("/a/"));
        assertTrue(fs.fileExists(new VariableAutomaton("/a/")));

        assertThrows(FileStructureException.class, () -> {
            // attempt to create file at existing directory '/a' (should fail)
            fs.createFile(new VariableAutomaton("/a"));
        });
    }

    @Test
    public void testCreateDirectoryWhenFileAlreadyExists() throws FileStructureException {
        // create and assert '/a'
        fs.createFile(new VariableAutomaton("/a"));
        assertTrue(fs.fileExists(new VariableAutomaton("/a")));

        assertThrows(FileStructureException.class, () -> {
            // attempt to create directory at existing file '/a' (should fail)
            fs.createDirectory(new VariableAutomaton("/a/"));
        });
    }

    @Test
    public void testIsDirectoryAndIsRegularFile() throws FileStructureException {
        mkdir("/dir1");
        assertTrue(fs.isDirectory(new VariableAutomaton("/dir1")));
        assertTrue(fs.isDirectory(new VariableAutomaton("/dir1/")));
        assertFalse(fs.isRegularFile(new VariableAutomaton("/dir1")));
        assertFalse(fs.isRegularFile(new VariableAutomaton("/dir1/")));
        assertFalse(fs.isDirectory(new VariableAutomaton("/dir1/blah")));
        assertFalse(fs.isDirectory(new VariableAutomaton("/dir1blah")));

        fs = new FileStructure();
        touch("/file1");
        assertFalse(fs.isDirectory(new VariableAutomaton("/file1")));
        assertFalse(fs.isDirectory(new VariableAutomaton("/file1/")));
        assertTrue(fs.isRegularFile(new VariableAutomaton("/file1")));
        assertTrue(fs.isRegularFile(new VariableAutomaton("/file1/")));
        assertFalse(fs.isRegularFile(new VariableAutomaton("/file1blah")));
    }

    @Test
    public void testRemoveFile() throws FileStructureException {
        touch("/file1");
        assertTrue(exists("/file1"));
        remove("/file1");
        save(fs.files, "tmp/result1.dot");
        assertFalse(exists("/file1"));
        assertTrue(exists("/"));

        fs = new FileStructure();
        mkdir("/a");
        touch("/a/b");
        assertTrue(exists("/a"));
        remove("/a/b");
        save(fs.files, "tmp/result2.dot");
        assertFalse(exists("/a/b"));
        assertTrue(exists("/a"));
    }

    @Test
    public void testRemoveDirectory() throws FileStructureException {
        mkdir("/a/b/c");
        touch("/a/b/c/d");
        touch("/a/b/c/e");
        touch("/a/b/c/f");
        removeRecursive("/a/b/c/");
        save(fs.files, "tmp/remove_directory.dot");
        assertTrue(exists("/a/b"));
        assertFalse(exists("/a/b/c/d"));
        assertFalse(exists("/a/b/c"));
    }

    @Test
    public void testChangeDirectory() throws FileStructureException {
        mkdir("/a");
        cd("/a");
        touch("b");
        save(fs.files, "tmp/change_directory.dot");
        assertTrue(fs.isDirectory(new VariableAutomaton("/")));
        assertTrue(fs.isDirectory(new VariableAutomaton("/a")));
        assertTrue(fs.isRegularFile(new VariableAutomaton("/a/b")));
    }

    void cd(String fp) throws FileStructureException {
        fs.changeWorkingDirectory(regex(fp));
    }

    void touch(String fp) throws FileStructureException {
        fs.createFile(regex(fp));
    }

    void mkdir(String fp) throws FileStructureException {
        fs.createDirectory(regex(fp));
    }

    void copy(String src, String dest) throws FileStructureException {
        fs.copy(new VariableAutomaton(src), new VariableAutomaton(dest));
    }

    void remove(String fp) throws FileStructureException {
        fs.removeFile(regex(fp));
    }

    void removeRecursive(String fp) throws FileStructureException {
        fs.removeFileRecursive(regex(fp));
    }

    boolean exists(String fp) {
        return fs.fileExists(new VariableAutomaton(fp));
    }

    // returns a variable automaton given a regex
    VariableAutomaton regex(String regex) {
        return new VariableAutomaton(new RegExp(regex).toAutomaton());
    }

    void save(Automaton a, String filepath) {
        GraphvizGenerator.saveDOTToFile(a.toDot(), filepath);
    }

}
