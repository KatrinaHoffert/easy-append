package com.mikehoffert.easyappend.control;

/**
 * Specifies a class as being observable, meaning that observers can be attached
 * to it and be notified by this object. This is used instead of Java's
 * {@link java.util.Observable} class because an interface is desired.
 */
public interface Observable
{
	/**
	 * Attaches an observer to this object.
	 * @param observer The observer to attach.
	 */
	public void attach(Observer observer);
}