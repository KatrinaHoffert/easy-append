package com.mikehoffert.easyappend.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mikehoffert.easyappend.control.Controller;
import com.mikehoffert.easyappend.control.Message;
import com.mikehoffert.easyappend.control.Observer;
import com.mikehoffert.easyappend.control.TextAddition;
import com.mikehoffert.easyappend.model.BufferedFile;

/**
 * A command line interface for interacting with the control classes.
 */
public class CommandLineInterface implements Observer
{
	private static final int LINE_WIDTH = 80;
	
	/**
	 * The controller used to interact with the system.
	 */
	private Controller controller = new Controller();
	
	/**
	 * Keeps track of whether or not arguments are malformed.
	 */
	private boolean malformedArguments = false;
	
	/**
	 * The exit status code that will be returned when the program exits. Non-zero
	 * indictates an error has occurred.
	 */
	private int exitStatus = 0;
	
	/**
	 * Whether or not to print out additional information.
	 */
	private boolean verbose = false;
	
	/**
	 * If true, adding a folder to the file list will add all files inside that
	 * folder.
	 */
	private boolean recursiveMode = false;
	
	/**
	 * True if running a test and program shouldn't exit (even if error occurred).
	 */
	private static boolean testing = false;
	
	/**
	 * The charset to use for all files. Defaults to UTF-8.
	 */
	private Charset charset = StandardCharsets.UTF_8;
	
	public static void main(String[] args)
	{
		CommandLineInterface cli = new CommandLineInterface();
		cli.parseArguments(args);
		cli.writeFiles();
		cli.exit();
	}
	
	public CommandLineInterface()
	{
		controller.attach(this);
	}
	
	/**
	 * Sets the CLI into testing mode.
	 * @param testing Whether or not we're in testing mode.
	 */
	public static void setTesting(boolean testing)
	{
		CommandLineInterface.testing = testing;
	}

	/**
	 * Parses the arguments supplied to the CLI and issues appropriate
	 * commands based on these arguments.<p>
	 * 
	 * Arguments should be in the form: <tt>--prepend [--contains=&lt;regex&gt;
	 * [--invert]] &lt;prepended text&gt; &lt;list of files&gt;</tt>. The
	 * <tt>--prepend</tt> "blocks" may be replaced with <tt>--append</tt>. There
	 * may be any number of these blocks (they will stack in order).<p>
	 * 
	 * As well, the <tt>--verbose</tt> flag will enable additional output about
	 * what the program is (or will) be doing, such as what files are being
	 * skipped and what files have been written so far. It will include output
	 * for each file regarding the application of each text addition (which are
	 * numbered in the order that they appear).<p>
	 * 
	 * The <tt>--dry-run</tt> flag will disable the writing of the files,
	 * allowing you to see what the program will do without side effects. This
	 * will automatically enable <tt>--verbose</tt>.
	 * @param args Command line arguments.
	 */
	private void parseArguments(String[] args)
	{
		boolean filesOnly = false;
		
		for(int i = 0; i < args.length; i++)
		{
			if(!filesOnly && args[i].equals("--append"))
			{
				i = createTextAddition(args, i, false);
			}
			else if(!filesOnly && args[i].startsWith("--charset"))
			{
				String[] charsetString = args[i].split("=");
				
				if(charsetString.length > 0)
				{
					try
					{
						// Set the charset, if it exists
						charset = Charset.forName(charsetString[1]);
						controller.setCharset(charset);
					}
					catch(IllegalArgumentException e)
					{
						System.err.println("The supplied charset does not exist.");
						malformedArguments = true;
					}
				}
				else
				{
					malformedArguments = true;
				}
			}
			else if(!filesOnly && args[i].equals("--dry-run"))
			{
				controller.setDryRun(true);
				verbose = true;
			}
			else if(!filesOnly && (args[i].equals("--help") || args[i].equals("-h")))
			{
				displayHelp();
			}
			else if(!filesOnly && args[i].startsWith("--location"))
			{
				String[] locationString = args[i].split("=");
				
				if(locationString.length > 0)
				{
					controller.setLocation(Paths.get(locationString[1]));
				}
				else
				{
					malformedArguments = true;
				}
			}
			else if(!filesOnly && args[i].equals("--prepend"))
			{
				i = createTextAddition(args, i, true);
			}
			else if(!filesOnly && (args[i].equals("--recursive") || args[i].equals("-r")))
			{
				recursiveMode = true;
			}
			else if(!filesOnly && (args[i].equals("--verbose") || args[i].equals("-v")))
			{
				verbose = true;
			}
			else if(!filesOnly && args[i].equals("--"))
			{
				// Symbolizes that all further tokens must be file names
				filesOnly = true;
			}
			// If this is encountered, there must be some argument that is in
			// the form of a flag, but is not a valid flag. In which case, we
			// assume that the arguments are malformed. Files that have names
			// like flags need to be separated from the flags with a `--`. This
			// is a security feature to prevent typos from globbing files.
			else if(!filesOnly && args[i].startsWith("--"))
			{
				malformedArguments = true;
			}
			else
			{
				File file = new File(args[i]);
				if(file.isFile())
				{
					controller.addFile(new BufferedFile(file));
				}
				else if(file.isDirectory())
				{
					// We only allow specifying directories if recursive mode is
					// on. This is a safety feature to prevent typos (etc) from
					// accidentally modifying a large number of files.
					if(recursiveMode)
					{
						Collection<File> contents = FileUtils.listFiles(file, null, true);
						for(File content : contents)
						{
							controller.addFile(new BufferedFile(content));
						}
					}
					else
					{
						System.err.println("In order to add directories, `--recursive`" +
								" must be enabled.");
						exitStatus = 5;
						exit();
					}
				}
				else
				{
					System.err.println("One or more of the file(s) to modify do not exist.");
					exitStatus = 2;
					exit();
				}
			}
			
			if(malformedArguments)
			{
				break;
			}
		}
	}
	
	/**
	 * Creates a text addition from a parition of the arguments.
	 * @param args The arguments array.
	 * @param i The current position in the array (should be an element with
	 * either <tt>--prepends</tt> or <tt>--appends</tt>.
	 * @param prepend Which kind of addition this is (determined by value at
	 * <tt>args[i]</tt>.
	 * @return New value of <tt>i</tt> after reading this block.
	 */
	private int createTextAddition(String[] args, int i, boolean prepend)
	{
		String text = null;
		String contains = null;
		boolean inverted = false;
		String textAdditionFile = null;
		
		// Determine what other arguments are set for this block
		if(i + 1 < args.length && args[i + 1].startsWith("--contains"))
		{
			// Everything after the equals sign
			// TODO: Use conditional here so that exception isn't thrown if
			// the arguments are malformed
			contains = args[++i].split("=")[1];
		}
		
		if(i + 1 < args.length && args[i + 1].equals("--invert"))
		{
			inverted = true;
			i++;
		}
		
		if(i + 1 < args.length && args[i + 1].startsWith("--file"))
		{
			// Everything after the equals sign
			textAdditionFile = args[++i].split("=")[1];
		}
		
		// Only read in the text if a file was not specified
		if(textAdditionFile == null && i + 1 < args.length)
		{
			text = args[++i];
		}
		// File was specified -- read it in as the text.
		else if(textAdditionFile != null)
		{
			try
			{
				text = FileUtils.readFileToString(new File(textAdditionFile), Charsets.UTF_8);
			}
			catch(IOException e)
			{
				System.err.println("One or more of the files contain text additions " +
						"could not be opened.");
				exitStatus = 4;
			}
		}
		else
		{
			malformedArguments = true;
		}
		
		controller.addText(new TextAddition(text, contains, inverted, prepend));
		
		return i;
	}
	
	/**
	 * Writes the files. Handles errors that may occur.
	 */
	private void writeFiles()
	{
		if(malformedArguments)
		{
			System.err.println("Invalid arguments.\n");
			displayHelp();
			exitStatus = 1;
			return;
		}
		
		try
		{
			controller.writeFiles();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("One or more of the file(s) to modify do not exist.");
			exitStatus = 2;
		}
		catch(IOException e)
		{
			System.err.println("Could not write the output file(s).");
			exitStatus = 3;
		}
	}
	
	/**
	 * Exits the program.
	 */
	private void exit()
	{
		if(!testing) System.exit(exitStatus);
	}
	
	/**
	 * Displays help on using the program.
	 */
	private void displayHelp()
	{
		// The help text content is located in a separate file, for ease of
		// formatting and modification.
		String text = "";
		try(Scanner scanner = new Scanner(new File("resource/help_text.txt")))
		{
			while(scanner.hasNextLine()) text += scanner.nextLine() + "\n";
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Couldn't open the help file. Something is horribly wrong.");
		}
		
		System.out.println(text);
		
		// Exit, as `--help` cannot be combined with other arguments
		exit();
	}

	@Override
	public void message(Message message)
	{
		if(verbose)
		{
			String output = TextWrapper.forWidth(LINE_WIDTH).hard()
					.setIndentLevel(message.getLevel() * 3).wrap(message.getMessage());
			System.out.println(output);
		}
	}
}
