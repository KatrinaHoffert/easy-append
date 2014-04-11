package edit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import edit.FileReading;

public class FileEditor
{
	private File file;
	private Charset charset;
	
	/**
	 * Initializes the file editor for a specific input file.
	 * @param file The file to edit.
	 */
	public FileEditor(File file, Charset charset)
	{
		this.file = file;
		this.charset = charset;
	}
	
	/**
	 * Appends text to the end of the file.
	 * @param text The text to append.
	 * @param newLine If true, append this text on a new line.
	 * @throws IOException The file cannot be modified.
	 */
	public void append(String text, boolean newLine) throws IOException
	{
		FileWriter writer = new FileWriter(file, true);
		
		if(newLine) writer.append('\n');
		
		writer.write(text);
		writer.close();
	}
	
	/**
	 * Prepends text to the start of the file.
	 * @param text The text to prepend.
	 * @param newLine If true, append a new line at the end of the prepended text.
	 * @throws IOException The file cannot be modified.
	 */
	public void prepend(String text, boolean newLine) throws IOException
	{
		String originalContents = FileReading.readContents(file, charset);
		
		FileWriter writer = new FileWriter(file);
		
		writer.write(text);
		
		if(newLine) writer.append('\n');
	
		writer.write(originalContents);
		writer.close();
	}
}