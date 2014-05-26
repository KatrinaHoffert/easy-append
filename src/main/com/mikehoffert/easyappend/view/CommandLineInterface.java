package com.mikehoffert.easyappend.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mikehoffert.easyappend.control.Controller;
import com.mikehoffert.easyappend.control.TextAddition;

/**
 * A command line interface for interacting with the control classes.
 */
public class CommandLineInterface
{
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
	 * indictates an error has occured.
	 */
	private int exitStatus = 0;
	
	public static void main(String[] args)
	{
		CommandLineInterface cli = new CommandLineInterface();
		cli.parseArguments(args);
		cli.writeFiles();
		cli.exit();
	}

	/**
	 * Parses the arguments supplied to the CLI and issues appropriate
	 * commands based on these arguments.<p>
	 * 
	 * Arguments should be in the form: <tt>--prepend [--contains=&lt;regex&gt;
	 * [--invert]] &lt;prepended text&gt; &lt;list of files&gt;</tt>. The
	 * <tt>--prepend</tt> "blocks" may be replaced with <tt>--append</tt>. There
	 * may be any number of these blocks (they will stack in order).
	 * @param args Command line arguments.
	 */
	private void parseArguments(String[] args)
	{
		boolean filesOnly = false;
		
		for(int i = 0; i < args.length; i++)
		{
			if(!filesOnly && args[i].equals("--prepend"))
			{
				i = createTextAddition(args, i, true);
			}
			else if(!filesOnly && args[i].equals("--append"))
			{
				i = createTextAddition(args, i, false);
			}
			else if(!filesOnly && args[i].equals("--"))
			{
				// Symbolizes that all further tokens must be file names
				filesOnly = true;
			}
			else
			{
				controller.addFile(new File(args[i]));
			}
			
			if(malformedArguments)
			{
				System.err.println("Invalid arguments.");
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
		String contains = "";
		boolean inverted = false;
		
		// Determine what other arguments are set for this block
		if(i + 1 < args.length && args[i + 1].startsWith("--contains"))
		{
			// Everything after the equals sign
			contains = args[++i].split("=")[1];
		}
		
		if(i + 1 < args.length && args[i + 1].equals("--invert"))
		{
			inverted = true;
			i++;
		}
		
		if(i + 1 < args.length)
		{
			text = args[++i];
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
		System.exit(exitStatus);
	}
}
