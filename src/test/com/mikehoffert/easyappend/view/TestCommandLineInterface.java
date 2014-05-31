package com.mikehoffert.easyappend.view;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCommandLineInterface
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	File file1;
	File file2;

	@Before
	public void setup() throws IOException
	{
		file1 = folder.newFile();
		
		FileWriter writer = new FileWriter(file1);
		writer.write("A\n");
		writer.write("B\n");
		writer.write("C");
		writer.close();
		
		file2 = folder.newFile();
		
		writer = new FileWriter(file2);
		writer.write("D\n");
		writer.write("E\n");
		writer.write("F");
		writer.close();
	}
	
	@Test
	public void test() throws FileNotFoundException
	{
		String[] args = {
			"--prepend",
			"--contains=^A$",
			"prepended1",
			"--prepend",
			"--contains=[C|D]",
			"prepended2",
			"--append",
			"--contains=D",
			"--invert",
			"appended1",
			"--append",
			"appended2",
			"--verbose",
			file1.toString(),
			file2.toString()
		};
		
		CommandLineInterface.setTesting(true);
		CommandLineInterface.main(args);
		
		// Verify that the output files match our expectations
		String[] expectedFile1 = {
			"prepended1",
			"prepended2",
			"A",
			"B",
			"C",
			"appended1",
			"appended2"
		};
		
		String[] expectedFile2 = {
			"prepended2",
			"D",
			"E",
			"F",
			"appended2"
		};
		
		Scanner scanner = new Scanner(file1);
		int i = 0;
		while(scanner.hasNextLine())
		{
			assertEquals(expectedFile1[i++], scanner.nextLine());
		}
		scanner.close();

		scanner = new Scanner(file2);
		i = 0;
		while(scanner.hasNextLine())
		{
			assertEquals(expectedFile2[i++], scanner.nextLine());
		}
		scanner.close();
	}
}
