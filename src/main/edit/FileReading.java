package edit;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class FileReading
{
	/**
	 * Converts a file into a string.
	 * @param file The file to read.
	 * @param encoding The encoding of the file.
	 * @return A string representing the file's contents.
	 * @throws IOException The file could not be read.
	 */
	public static String readContents(File file, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(file.toPath());
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
