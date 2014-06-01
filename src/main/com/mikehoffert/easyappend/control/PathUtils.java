package com.mikehoffert.easyappend.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mikehoffert.easyappend.model.BufferedFile;

/**
 * Provides utilities for working with paths.
 */
public class PathUtils
{
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
	public static File determineFileLocation(File file, Path baseDirectory, Path location) throws IOException
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

	/**
	 * Determines a common, base path that all files have. If the files are on
	 * different drives, the drive letter is included on the path.
	 * @param files The files to determine a common base path for.
	 * @return The common base path, if it exists, or null if there is no common
	 * path (meaning that the files exist on different roots).
	 * @throws IOException Failed to determine the paths of some file(s). Note:
	 * does not mean that the files do or do not exist.
	 */
	public static Path determineCommonPath(List<BufferedFile> files) throws IOException
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
}
