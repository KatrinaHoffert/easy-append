package com.mikehoffert.easyappend.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CommandLineInterface
{
	private Controller controller = new Controller();
	private boolean malformedArguments = false;
	private int exitStatus = 0;
	
	public static void main(String[] args)
	{
		CommandLineInterface cli = new CommandLineInterface();
		cli.parseArguments(args);
		cli.writeFiles();
		cli.exit();
	}

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
			else
			{
				// Symbolizes that all further tokens must be file names
				filesOnly = true;

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
	
	private void exit()
	{
		System.exit(exitStatus);
	}
}
