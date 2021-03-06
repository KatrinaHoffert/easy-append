package com.mikehoffert.easyappend.control;

/**
 * A simple class for collecting information needed to add text to a file.
 */
public class TextAddition
{
	/**
	 * The text to prepend or append.
	 */
	private String text;
	
	/**
	 * An optional regex that the file must have in order to make this addition.
	 * Set to null to not require any containing regex.
	 */
	private String contains;
	
	/**
	 * If true, the contains is inverted. ie, the file must NOT contain the
	 * regex.
	 */
	private boolean inverted;
	
	/**
	 * If true, prepends the text. If false, appends.
	 */
	private boolean prepend;
	
	/**
	 * If true, text is on the same line as an existing one. If false, the
	 * addition is placed on its own line.
	 */
	private boolean sameLine;

	/**
	 * Groups properties of adding text.
	 * @param text The text to prepend or append.
	 * @param contains An optional regex that the file must have in order to
	 * make this addition. Set to null to not require any containing regex.
	 * @param inverted If true, the contains is inverted. ie, the file must\
	 * NOT contain the regex.
	 * @param prepend If true, prepends the text. If false, appends.
	 */
	public TextAddition(String text, String contains, boolean inverted,
			boolean prepend, boolean sameLine)
	{
		this.text = text;
		this.contains = contains;
		this.inverted = inverted;
		this.prepend = prepend;
		this.sameLine = sameLine;
	}
	
	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getContains()
	{
		return contains;
	}

	public void setContains(String contains)
	{
		this.contains = contains;
	}

	public boolean isInverted()
	{
		return inverted;
	}

	public void setInverted(boolean invert)
	{
		this.inverted = invert;
	}

	public boolean isPrepend()
	{
		return prepend;
	}

	public void setPrepend(boolean prepend)
	{
		this.prepend = prepend;
	}

	public boolean isSameLine()
	{
		return sameLine;
	}
	
	public void setSameLine(boolean sameLine)
	{
		this.sameLine = sameLine;
	}
}
