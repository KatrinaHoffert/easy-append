package com.mikehoffert.easyappend.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

/**
 * Model class representing the file being modified. File content is not read
 * until it is needed.
 */
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
	 * Charset the file is assume to use.
	 */
	private Charset charset;
	
	/**
	 * Initializes the buffered file.
	 * @param file The file being modified.
	 */
	public BufferedFile(File file)
	{
		this.file = file;
	}
	
	/**
	 * Sets the charset (encoding) of the file.
	 * @param charset The charset to use.
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset;
	}
	
	/**
	 * Sets the text to be prepended. Prepending when there is already text
	 * to prepend will append to that text. If passed <tt>null</tt>, will
	 * not prepend anything.
	 * @param prependText The text.
	 */
	public void setPrependText(String prependText, boolean sameLine)
	{
		// Case for overwriting existing text with null
		if(prependText == null)
		{
			this.prependText = prependText;
		}
		// Case for adding to existing text
		if(this.prependText != null)
		{
			this.prependText += prependText;
			if(!sameLine) this.prependText += "\n";
		}
		// Case for setting text the first time
		else
		{
			this.prependText = prependText;
			if(!sameLine) this.prependText += "\n";
		}
	}

	/**
	 * Sets the text to be appended. Appending when there is already text
	 * to append will append to that text. If passed <tt>null</tt>, will
	 * not append anything.
	 * @param appendText The text.
	 */
	public void setAppendText(String appendText, boolean sameLine)
	{
		// Same cases as in `setPrependText(String)`.
		if(appendText == null)
		{
			this.appendText = appendText;
		}
		if(this.appendText != null)
		{
			if(!sameLine) this.appendText += "\n";
			this.appendText += appendText;
		}
		else
		{
			if(sameLine)
			{
				this.appendText = appendText;
			}
			else
			{
				this.appendText = "\n" + appendText;
			}
		}
	}
	
	public File getFile()
	{
		return file;
	}
	
	/**
	 * Writes the file to the specified location. Will overwrite if a file
	 * already exists at that location. The prepended and appended text
	 * will be automatically added.
	 * @param outputFile The file to output.
	 * @param observers The observers to notify progress for.
	 * @throws FileNotFoundException The file we're appending or prepending to
	 * does not exist.
	 * @throws IOException Could not write to the desired file.
	 */
	public void write(File outputFile) throws FileNotFoundException, IOException
	{
		if(contents == null) contents = FileUtils.readFileToString(file, charset);
		
		FileOutputStream fos = FileUtils.openOutputStream(outputFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter writer = new BufferedWriter(osw);
		
		if(prependText != null) writer.write(prependText);
		
		writer.write(contents);

		if(appendText != null) writer.write(appendText);
		
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
		if(contents == null) contents = FileUtils.readFileToString(file, charset);

		// We don't need a complete match, so there may be any text on either
		// side of the regex. The (?m) enables multi-line mode (so that `^` and
		// `$` can match beginning and end of lines) and the (?s) allows the dot
		// to match new lines.
		return contents.matches("(?m)(?s).*" + regex + ".*");
	}
	
	@Override
	public String toString()
	{
		return file.toString();
	}
}
