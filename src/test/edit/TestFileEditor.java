package edit;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edit.FileEditor;

public class TestFileEditor
{
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	final Charset CHARSET = Charset.defaultCharset();
    
	FileEditor editor1;
	FileEditor editor2;
	FileEditor editor3;
	FileEditor editor1Copy;
	FileEditor editor2Copy;
	File file1;
	File file2;
	File file3;
	File file1Copy;
	File file2Copy;
	
	@Before
	public void setup() throws IOException
	{
		// Create a temporary file to append to
		file1 = folder.newFile("append1.txt");
		FileWriter writer = new FileWriter(file1);
        writer.write("Foo\n");
        writer.write("Bar");
        writer.close();
        
        editor1 = new FileEditor(file1, CHARSET);

        // And a blank file
		file2 = folder.newFile("append2.txt");
		writer = new FileWriter(file2);
        writer.close();

        editor2 = new FileEditor(file2, CHARSET);
        
        // Finally, a non-existant file
        file3 = folder.getRoot().toPath().resolve("non-existant.txt").toFile();
        editor3 = new FileEditor(file3, CHARSET);
        
        // Make copies of files 1 and 2 for prepending
        file1Copy = folder.newFile("prepend1.txt");
        String file1Contents = FileReading.readContents(file1, CHARSET);
		writer = new FileWriter(file1Copy);
		writer.write(file1Contents);
        writer.close();

        file2Copy = folder.newFile("prepend2.txt");
        String file2Contents = FileReading.readContents(file2, CHARSET);
		writer = new FileWriter(file2Copy);
		writer.write(file2Contents);
        writer.close();
        
        editor1Copy = new FileEditor(file1Copy, CHARSET);
        editor2Copy = new FileEditor(file2Copy, CHARSET);
	}
	
	@Test
	public void testAppend() throws IOException
	{
		// Test appending with a new line being inserted
		editor1.append("Test", true);
		String contents = FileReading.readContents(file1, CHARSET);
		assertEquals(contents, "Foo\nBar\nTest");
		
		// Test appending without new line insertion
		editor1.append("More", false);
		contents = FileReading.readContents(file1, CHARSET);
		assertEquals(contents, "Foo\nBar\nTestMore");
		
		// Test appending to blank file
		editor2.append("Foo", false);
		contents = FileReading.readContents(file2, CHARSET);
		assertEquals(contents, "Foo");
		
		// Test appending to non-existant file
		editor3.append("Bar", false);
		contents = FileReading.readContents(file3, CHARSET);
		assertEquals(contents, "Bar");
	}
	
	@Test
	public void testPrepend() throws IOException
	{
		// Test prepending with new line being inserted
		editor1Copy.prepend("Foo", true);
		String contents = FileReading.readContents(file1Copy, CHARSET);
		assertEquals(contents, "Foo\nFoo\nBar");

		// Test prepending without new line being inserted
		editor1Copy.prepend("Foo", false);
		contents = FileReading.readContents(file1Copy, CHARSET);
		assertEquals(contents, "FooFoo\nFoo\nBar");
		
		// Test prepending into blank file
		editor2Copy.prepend("Foo", false);
		contents = FileReading.readContents(file2Copy, CHARSET);
		assertEquals(contents, "Foo");
	}
}
