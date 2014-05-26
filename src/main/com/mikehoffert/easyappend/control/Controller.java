package com.mikehoffert.easyappend.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mikehoffert.easyappend.model.BufferedFile;

/**
 * Provides interaction with the model classes.
 */
public class Controller
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
	 * Adds a new file to the list of files to (potentially) modify.
	 * @param file The file to add.
	 */
	public void addFile(File file)
	{
		files.add(new BufferedFile(file));
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
			for(TextAddition addition : additions)
			{
				// Determine if the file contains any required regex
				boolean applyChange = true;
				if(addition.getContains() != null)
				{
					boolean contains = file.contains(addition.getContains());
					applyChange = contains && !addition.isInverted();
				}
				
				if(applyChange)
				{
					if(addition.isPrepend())
					{
						file.setPrependText(addition.getText());
					}
					else
					{
						file.setAppendText(addition.getText());
					}
				}
			}
		
			file.write(file.getFile());
		}
	}
}
