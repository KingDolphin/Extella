package com.leonmontealegre.utils;

/**
 * Thrown by {@code Input} methods to indicate that a mouse
 * button is either negative or greater than or equal to 5.
 * 
 * @author Leon Montealegre
 * @see com.leonmontealegre.input.Input#getMouse(int)
 * @see com.leonmontealegre.input.Input#getMouseDown(int)
 * @see com.leonmontealegre.input.Input#getMouseUp(int)
 */
public class MouseButtonOutOfBoundsException extends IndexOutOfBoundsException {
	
	private static final long serialVersionUID = 8094301139284965845L;
	
	/**
	 * Constructs a {@code MouseButtonOutOfBoundsException} with no
	 * detail message.
	 */
	public MouseButtonOutOfBoundsException() {
		super();
	}
	
	/**
	 * Constructs a {@code MouseButtonOutOfBoundsException} with
	 * the specified detail message.
	 * 
	 * @param	s
	 * 			The detail message.
	 */
	public MouseButtonOutOfBoundsException(String s) {
		super(s);
	}
	
	/**
	 * Constructs a new {@code MouseButtonOutOfBoundsException}
	 * class with an argument indicating the illegal index.
	 * 
	 * @param	index
	 * 			The illegal index.
	 */
	public MouseButtonOutOfBoundsException(int index) {
		super("Not a mouse button: " + index);
	}
	
}
