package com.mikehoffert.easyappend.control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.mikehoffert.easyappend.model.BufferedFile;

/**
 * Provides interaction with the model classes.
 */
public class Controller implements Observable
{
	/**
	 * All the files that we are modifying.
	 */
	private List<BufferedFile> files = new ArrayList<>();
	
	/**
	 * All the additions that are being applied to files.
	 */
	private List<TextAddition> additions = new ArrayList<>();
	
	/**
	 * All observers of this class.
	 */
	private List<Observer> observers = new ArrayList<>();
	
	/**
	 * If true, does everything *except* write the files.
	 */
	private boolean dryRun = false;
	
	/**
	 * Adds a new file to the list of files to (potentially) modify.
	 * @param file The file to add.
	 */
	public void addFile(BufferedFile file)
	{
		files.add(file);
	}
	
	/**
	 * Adds a new text addition that can modify the files. The text additions
	 * are applied in the order that they are added.
	 * @param addition The addition to add.
	 */
	public void addText(TextAddition addition)
	{
		additions.add(addition);
	}
	
	@Override
	public void attach(Observer observer)
	{
		observers.add(observer);
	}
	
	/**
	 * Sets whether or not to perform a dry run, which will not write the files.
	 * @param dryRun True if a dry run.
	 */
	public void setDryRun(boolean dryRun)
	{
		this.dryRun = dryRun;
	}
	
	/**
	 * Used to send messages to all observers with a single method.
	 * @param message The message to send.
	 */
	private void messageAllObservers(String message, int level)
	{
		for(Observer observer : observers)
		{
			observer.message(new Message(message, level));
		}
	}
	
	/**
	 * Applies all additions to all files.
	 * @throws FileNotFoundException The file we're appending or prepending to
	 * does not exist.
	 * @throws IOException Could not write to the desired file.
	 */
	public void writeFiles() throws FileNotFoundException, IOException
	{
		for(BufferedFile file : files)
		{
			messageAllObservers("Working on file " + file, 0);
			int counter = 0;
			for(TextAddition addition : additions)
			{
				counter++;
				messageAllObservers("Evaluating text addition #" + counter +
						" (" + (addition.isPrepend() ? "prepend" : "append") +  ")", 1);
				
				// Determine if the file contains any required regex
				boolean applyChange = true;
				if(addition.getContains() != null)
				{
					boolean contains = file.contains(addition.getContains());
					applyChange = contains ^ addition.isInverted();
					
					messageAllObservers("File " + (contains ? "does" : "does not") +
							" contain the regex.", 2);
					
					if(!applyChange)
					{
						messageAllObservers("Skipping because regex should" +
								(addition.isInverted() ? " not" : "") + " be matched.", 2);
					}
				}
				
				if(applyChange)
				{
					if(addition.isPrepend())
					{
						file.setPrependText(addition.getText());
						messageAllObservers("Text will be prepended.", 2);
					}
					else
					{
						file.setAppendText(addition.getText());
						messageAllObservers("Text will be appended.", 2);
					}
				}
			}
		
			if(!dryRun) file.write(file.getFile(), observers);
		}
	}
}
