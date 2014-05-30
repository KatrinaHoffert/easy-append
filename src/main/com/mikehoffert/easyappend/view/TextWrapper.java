/*
 * This class was originally created by Sean Patrick Floyd
 *     <http://stackoverflow.com/users/342852/sean-patrick-floyd>
 *     Posted at <http://stackoverflow.com/a/5689524/1968462>
 * As with all code posted to StackOverflow (at least at the time that the code
 * was obtained), the code is licensed under CC-BY-SA 3.0.
 * 
 * This class has since been extended by Mike Hoffert. Due to the buggy nature
 * of the soft wrapping, it has been reduced to a hard wrap-only, with a smaller
 * size since strategies are no longer needed.
 */

package com.mikehoffert.easyappend.view;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * An immutable class for performing text wrapping.
 */
public class TextWrapper
{
	/**
	 * The delimiter to use for splitting the text into words.
	 */
	final private CharMatcher delimiter;

	/**
	 * The maximum width of the text.
	 */
	final private int width;
	
	/**
	 * An optional level of indentation.
	 */
	final private int indentLevel;

	/**
	 * Initializes the text wrapper with the appropriate strategy.
	 * @param strategy The strategy to use.
	 * @param delimiter The delimiter to break words up with.
	 * @param width The maximum width of the lines.
	 */
	private TextWrapper(CharMatcher delimiter, int width, int indentLevel)
	{
		this.delimiter = delimiter;
		this.width = width;
		this.indentLevel = indentLevel;
	}

	/**
	 * Creates a wrapper for the desired width.
	 * @param i The maximum width of the lines.
	 * @return The text wrapper.
	 */
	public static TextWrapper forWidth(int i)
	{
		return new TextWrapper(CharMatcher.WHITESPACE, i, 0);
	}

	/**
	 * Enables hard wrapping.
	 */
	public TextWrapper hard()
	{
		return new TextWrapper(this.delimiter, this.width, this.indentLevel);
	}
	
	/**
	 * Sets the indentation level of the wrapper, in spaces.
	 */
	public TextWrapper setIndentLevel(int indentLevel)
	{
		return new TextWrapper(this.delimiter, this.width, indentLevel);
	}

	/**
	 * Wraps the desired text.
	 * @param text The text to wrap.
	 * @return The wrapped text.
	 */
	public String wrap(final String text)
	{
		String indent = new String(new char[indentLevel]).replace("\0", " ");
		return indent + Joiner.on("\n" + indent).join(Splitter.fixedLength(width -
				indentLevel).split(Joiner.on(' ').join(Splitter.on(this.delimiter).
				split(text))));
	}
}