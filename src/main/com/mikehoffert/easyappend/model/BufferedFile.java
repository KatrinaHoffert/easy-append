package com.mikehoffert.easyappend.model;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.common.io.CharStreams;

public class BufferedFile
{
	/**
	 * The file being (possibly) modified.
	 */
	private File file;
	
	/**
	 * Text to be prepended.
	 */
	private String prependText;
	
	/**
	 * Text to be appended.
	 */
	private String appendText;
	
	/**
	 * The file contents
	 */
	private String contents;
	
	/**
	 * Initializes the buffered file.
	 * @param file The file being modified.
	 */
	public BufferedFile(File file)
	{
		this.file = file;
	}
	
	/**
	 * Sets the text to be prepended. Set to null to not prepend any text.
	 * @param prependText The text.
	 */
	public void setPrependText(String prependText)
	{
		this.prependText = prependText;
	}

	/**
	 * Sets the text to be appended. Set to null to not append any text.
	 * @param prependText The text.
	 */
	public void setAppendText(String appendText)
	{
		this.appendText = appendText;
	}
	
	/**
	 * Writes the file to the specified location. Will overwrite if a file
	 * already exists at that location. The prepended and appended text
	 * will be automatically added.
	 * @param outputFile The file to output.
	 * @throws FileNotFoundException The file we're appending or prepending to
	 * does not exist.
	 * @throws IOException Could not write to the desired file.
	 */
	public void write(File outputFile) throws FileNotFoundException, IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		if(prependText != null) writer.write(prependText + "\n");
		
		if(contents == null) contents = readFile();
		writer.write(contents);

		if(appendText != null) writer.write(appendText + "\n");
		
		writer.close();
	}
	
	/**
	 * Determines if the file contains a particular regex.
	 * @param regex The regex to attempt to match.
	 * @return True if the regex was matched somewhere in the file, false
	 * otherwise.
	 * @throws FileNotFoundException The file we're appending or prepending to
	 * does not exist.
	 * @throws IOException Could not write to the desired file.
	 */
	public boolean contains(String regex) throws FileNotFoundException, IOException
	{
		if(contents == null) contents = readFile();

		// We don't need a complete match, so there may be any text on either
		// side of the regex. The (?m) enables multi-line mode (so that `^` and
		// `$` can match beginning and end of lines) and the (?s) allows the dot
		// to match new lines.
		return contents.matches("(?m)(?s).*" + regex + ".*");
	}
	
	/**
	 * Reads the file into a single string.
	 * @return The contents of the file.
	 * @throws FileNotFoundException File does not exist.
	 * @throws IOException File could not be read.
	 */
	private String readFile() throws FileNotFoundException, IOException
	{
		List<String> lines = CharStreams.readLines(new FileReader(file));
		
		String allLines = "";
		for(String line : lines)
		{
			allLines += line + "\n";
		}
		
		return allLines.substring(0, allLines.length());
	}
}
