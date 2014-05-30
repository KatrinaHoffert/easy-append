package com.mikehoffert.easyappend.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
	 * Location to place written files. If null, use their current locations.
	 */
	private Path location = null;
	
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
		Path commonPath = determineCommonPath(files);
		// Only print out path info if we specified an alternative location to
		// write to
		if(location != null)
		{
			if(commonPath == null)
			{
				messageAllObservers("Files do not share a common path", 0);
			}
			else
			{
				messageAllObservers("Common path that files share is: " + commonPath, 0);
			}
		}
		
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
		
			File outputFile = determineFileLocation(file.getFile(), commonPath, location);
			messageAllObservers("File will be written to: " + outputFile.toString(), 1);
			if(!dryRun) file.write(outputFile);
			
			messageAllObservers("File written.", 1);
		}
	}
	
	/**
	 * Sets the location to place the output files in.
	 * @param location The location to place output files.
	 */
	public void setLocation(Path location)
	{
		this.location = location;
	}
	
	/**
	 * Determines a common, base path that all files have. If the files are on
	 * different drives, the drive letter is included on the path.
	 * @param files The files to determine a common base path for.
	 * @return The common base path, if it exists, or null if there is no common
	 * path (meaning that the files exist on different roots).
	 * @throws IOException Failed to determine the paths of some file(s). Note:
	 * does not mean that the files do or do not exist.
	 */
	private Path determineCommonPath(List<BufferedFile> files) throws IOException
	{
		Path baseDirectory = null;
		
		for(BufferedFile file : files)
		{
			Path path = file.getFile().getCanonicalFile().toPath().getParent();

			// First proper path we've encountered
			if(baseDirectory == null)
			{
				baseDirectory = path;
				continue;
			}
			// Paths have different root, so we have the longest possible base
			// directory, containing all roots.
			else if(!baseDirectory.getRoot().equals(path.getRoot()))
			{
				return null;
			}
			// Paths are equal, do nothing
			else if(baseDirectory.equals(path))
			{
				continue;
			}
			// Figure out which path is shorter
			else
			{
				Path relative1to2 = baseDirectory.relativize(path);
				Path relative2to1 = path.relativize(baseDirectory);
				
				// Case where paths are mutual
				if(relative1to2.toString().contains("..") &&
						relative2to1.toString().contains(".."))
				{
					int dirUps = StringUtils.countMatches(relative1to2.toString(), "..");
					for(int i = 0; i < dirUps; i++)
					{
						baseDirectory = baseDirectory.resolve("../");
					}
				}
				// base directory is "deeper" than path
				else if(relative1to2.toString().contains(".."))
				{
					baseDirectory = path;
				}
				// Otherwise the path is deeper than the current base, so
				// the working base is still the optimal base path
			}
		}
		
		return baseDirectory.toFile().getCanonicalFile().toPath();
	}
	
	/**
	 * Determines where to write a file to based on its location, the base
	 * directory that all files share, and an optional folder to write the
	 * files into (instead of overwriting existing files).
	 * @param file The file in question. We're finding the location to save the
	 * modified version of this file.
	 * @param baseDirectory The base directory that all files share. Will be
	 * null if there is no base directory (files are on different roots).
	 * @param location The location to save all files.
	 * @return File pertaining to where the file should be written.
	 * @throws IOException 
	 */
	private File determineFileLocation(File file, Path baseDirectory, Path location) throws IOException
	{
		// Overwriting existing files
		if(location == null)
		{
			return file;
		}
		
		Path relativeFromLocation;
		if(baseDirectory != null)
		{
			relativeFromLocation = baseDirectory.relativize(file.toPath());
		}
		else
		{
			Path root = file.getCanonicalFile().toPath().getRoot();
			String rootName = root.toString().substring(0, 1).toLowerCase();
			Path relativeFromRoot = root.relativize(file.toPath().toAbsolutePath());
			relativeFromLocation = Paths.get(rootName).resolve(relativeFromRoot);
		}
		
		return location.resolve(relativeFromLocation).toFile().getCanonicalFile();
	}
}
