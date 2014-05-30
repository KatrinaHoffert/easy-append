/*
 * This class was originally created by Sean Patrick Floyd
 *     <http://stackoverflow.com/users/342852/sean-patrick-floyd>
 *     Posted at <http://stackoverflow.com/a/5689524/1968462>
 * As with all code posted to StackOverflow (at least at the time that the code
 * was obtained), the code is licensed under CC-BY-SA 3.0.
 * 
 * This class has since been extended by Mike Hoffert. This file remains under
 * CC-BY-SA 3.0, different from the rest of the project. Changes to this file
 * include modifications to organization and ability to add further delimiters
 * and set an indentation level for text.
 */

package com.mikehoffert.easyappend.view;

import java.util.Iterator;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * An immutable class for performing text wrapping.
 */
public class TextWrapper
{
	/**
	 * The wrap strategy to use.
	 */
	private WrapStrategy strategy;

	/**
	 * The delimiter to use for splitting the text into words.
	 */
	private CharMatcher delimiter;

	/**
	 * The maximum width of the text.
	 */
	private int width;
	
	/**
	 * An optional level of indentation.
	 */
	private int indentLevel;

	/**
	 * Defines the strategy to use for wrapping text.
	 */
	interface WrapStrategy
	{
		/**
		 * Provides the wrapping functionality.
		 * @param words A list of words to wrap.
		 * @param width The maximum line width.
		 * @param indentLevel The indentation to provide.
		 * @return A single string with appropriately placed line breaks.
		 */
		public String wrap(Iterable<String> words, int width, int indentLevel);
	}
	
	enum Strategy implements WrapStrategy
	{
		/**
		 * Provides hard wrapping, where each line (except the last) has exactly
		 * the specified width. Splitting is done at the <tt>width</tt> position
		 * in each line.
		 */
		HARD
		{
			@Override
			public String wrap(final Iterable<String> words, final int width,
					final int indentLevel)
			{
				String indent = new String(new char[indentLevel]).replace("\0", " ");
				return indent + Joiner.on("\n" + indent).join(Splitter.fixedLength(width -
						indentLevel).split(Joiner.on(' ').join(words)));
			}
		},
		/**
		 * Provides soft wrapping, where each line will be no longer than the
		 * specified width, but may be shorter. Will attempt to split at the
		 * location of the last delimiter.
		 */
		SOFT
		{
			@Override
			public String wrap(final Iterable<String> words, final int width,
					final int indentLevel)
			{
				final StringBuilder sb = new StringBuilder();
				int lineLength = 0;
				final Iterator<String> iterator = words.iterator();
				String indent = new String(new char[indentLevel]).replace("\0", " ");

				if(iterator.hasNext())
				{
					sb.append(iterator.next());
					lineLength = sb.length();

					while(iterator.hasNext())
					{
						final String word = iterator.next();
						if(word.length() + 1 + lineLength + indentLevel > width)
						{
							sb.append('\n' + indent);
							lineLength = 0;
						}
						else
						{
							lineLength++;
							sb.append(' ');
						}
						sb.append(word);
						lineLength += word.length();
					}
				}
				return indent + sb.toString();
			}
		}
	}

	/**
	 * Initializes the text wrapper with the appropriate strategy.
	 * @param strategy The strategy to use.
	 * @param delimiter The delimiter to break words up with.
	 * @param width The maximum width of the lines.
	 */
	private TextWrapper(WrapStrategy strategy, CharMatcher delimiter, int width,
			int indentLevel)
	{
		this.strategy = strategy;
		this.delimiter = delimiter;
		this.width = width;
		this.indentLevel = indentLevel;
	}

	/**
	 * Creates a wrapper for the desired width.
	 * @param i The maximum width of the lines.
	 * @return The text wrapper.
	 */
	public static TextWrapper forWidth(final int i)
	{
		return new TextWrapper(Strategy.SOFT, CharMatcher.WHITESPACE, i, 0);
	}

	/**
	 * Enables hard wrapping.
	 */
	public TextWrapper hard()
	{
		return new TextWrapper(Strategy.HARD, this.delimiter, this.width, this.indentLevel);
	}

	/**
	 * Allows existing line breaks to be kept.
	 */
	public TextWrapper respectExistingBreaks()
	{
		return new TextWrapper(this.strategy, CharMatcher.anyOf(" \t"),
				this.width, this.indentLevel);
	}
	
	/**
	 * Adds an additional delimiter. All existing delimiters are also valid.
	 * @param delimiter Additional delimiter to use.
	 */
	public TextWrapper addDelimiter(String delimiter)
	{
		return new TextWrapper(this.strategy,
				this.delimiter.or(CharMatcher.anyOf(delimiter)), this.width,
				this.indentLevel);
	}
	
	/**
	 * Sets the indentation level of the wrapper, in spaces.
	 */
	public TextWrapper setIndentLevel(int indentLevel)
	{
		return new TextWrapper(this.strategy, this.delimiter, this.width, indentLevel);
	}

	/**
	 * Wraps the desired text.
	 * @param text The text to wrap.
	 * @return The wrapped text.
	 */
	public String wrap(final String text)
	{
		return this.strategy.wrap(Splitter.on(this.delimiter).split(text),
				this.width, this.indentLevel);
	}

}