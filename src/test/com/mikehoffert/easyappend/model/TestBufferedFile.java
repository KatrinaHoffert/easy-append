package com.mikehoffert.easyappend.model;

import static org.junit.Assert.*;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.CharStreams;

public class TestBufferedFile
{
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	File testFile;
	String[] content = {
		"Alpha",
		"Bravo",
		"Charlie",
		"Delta",
		"Echo"
	};
	
	@Before
	public void setup() throws IOException
	{
		testFile = folder.newFile();
		
		FileWriter writer = new FileWriter(testFile);
		
		// Write all but the first and last line
		for(int i = 1; i < content.length - 1; i++) writer.write(content[i] + "\n");
		writer.close();
	}
	
	@Test
	public void testPrep() throws IOException
	{
		BufferedFile bf = new BufferedFile(testFile);
		File output = folder.newFile();
		
		// Prepend the first line of content array
		bf.setPrependText(content[0]);
		bf.write(output);
		
		List<String> lines = CharStreams.readLines(new FileReader(output));
		
		for(int i = 0; i < content.length - 1; i++) assertEquals(lines.get(i), content[i]);
	}
	
	@Test
	public void testApp() throws IOException
	{
		BufferedFile bf = new BufferedFile(testFile);
		File output = folder.newFile();
		
		// Prepend the last line of content array
		bf.setAppendText(content[content.length - 1]);
		bf.write(output);
		
		List<String> lines = CharStreams.readLines(new FileReader(output));
		
		for(int i = 1; i < content.length; i++) assertEquals(lines.get(i - 1), content[i]);
	}
	
	@Test
	public void testPrepApp() throws IOException
	{
		BufferedFile bf = new BufferedFile(testFile);
		File output = folder.newFile();
		
		// Prepend and append the first and last line of content array,
		// respectively
		bf.setPrependText(content[0]);
		bf.setAppendText(content[content.length - 1]);
		bf.write(output);
		
		List<String> lines = CharStreams.readLines(new FileReader(output));
		
		for(int i = 0; i < content.length; i++) assertEquals(lines.get(i), content[i]);
	}
	
	@Test
	public void testContains() throws FileNotFoundException, IOException
	{
		BufferedFile bFile = new BufferedFile(testFile);
		
		assertTrue(bFile.contains("^Bravo$"));
		assertTrue(bFile.contains("^Charlie$"));
		assertTrue(bFile.contains("Bravo\nCharlie"));
		assertTrue(bFile.contains("Bravo.*Delta"));
		assertTrue(bFile.contains("[A-Za-z]"));
		assertTrue(bFile.contains("\\bBravo\\b"));
		assertFalse(bFile.contains("^Foo$"));
	}
}
