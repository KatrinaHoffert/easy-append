package com.mikehoffert.easyappend.control;

public class Message
{
	private String message;
	private int level;
	
	public Message(String message, int level)
	{
		this.message = message;
		this.level = level;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public String getMessage()
	{
		return message;
	}
}
