package com.mikehoffert.easyappend.control;

/**
 * An observer for the observer pattern. Use with {@link Observable}. Used
 * instead of {@link java.util.Observer} because it needs to interact
 * with out custom <tt>Observable</tt> interface.
 */
public interface Observer
{
	/**
	 * Used to send messages to the observer from the <tt>Observable</tt>.
	 * @param message The message to send. The type is implementation defined.
	 */
	public void message(Object message);
}
