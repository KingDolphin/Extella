package com.leonmontealegre.utils;

import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;

/**
 * The {@code Input} class is a utilities class that provides easy and simple to use keyboard and touch input.
 * 
 * <p>
 * This class is constant, there may only ever be a single instance of it that can be accessed statically.
 * To setup this class within LibGDX, in the create method of your Application, write this line of code :
 * <blockquote><pre>
 * {@code Gdx.input.setInputProcessor(Input.instance);}
 * </pre></blockquote>
 * <p>
 * The final setup step is to add this line of code to the 'update' method of your game. For most LibGDX users,
 * this will be your {@code render} method.
 * <blockquote><pre>
 * Input.update();
 * </pre></blockquote>
 * <p>
 * Now you are all setup and ready to accept input. Here are some examples of how:
 * <blockquote><pre>
 * if (Input.getKey(Key.W)) {
 *    //Do something <b>while</b> 'W' is being pressed
 * }
 * 
 * if (Input.getMouseDown(Input.LEFT_MOUSE_BUTTON)) {
 *    //Do something <b>when</b> the left mouse button is pressed
 * }
 * 
 * if (Input.getMouseUp(Input.RIGHT_MOUSE_BUTTON)) {
 *    //Do something <b>when</b> the right mouse button is released
 * }
 * </pre></blockquote>
 * <em>* Note that these examples should be inside your 'update' method</em>
 * 
 * @author Leon Montealegre
 * @version 0.03
 */
public final class Input implements InputProcessor {
	
	public static final int LEFT_MOUSE_BUTTON = 0;
	public static final int RIGHT_MOUSE_BUTTON = 1;
	public static final int MIDDLE_MOUSE_BUTTON = 2;
	public static final int BACK_MOUSE_BUTTON = 3;
	public static final int FORWARD_MOUSE_BUTTON = 4;
	
	/** Instance of Input to attach as the LibGDX listener. */
	public static final Input instance = new Input();
	
	/** Array of all the keys that have been pressed for the first time. */
	private static final boolean[] firstPressedKeys = new boolean[255];
	
	/** Array of all the keys that are being held. */
	private static final boolean[] pressedKeys = new boolean[255];
	
	/** Array of all the keys that have been released. */
	private static final boolean[] releasedKeys = new boolean[255];
	
	/** The current position of the mouse. */
	public static ArrayList<Touch> touches = new ArrayList<Touch>();
	
	private static ArrayList<Boolean> valuesToChange = new ArrayList<Boolean>();
	private static ArrayList<Integer> keysToChange = new ArrayList<Integer>();
	private static int scrollAmount = 0;
	private static Key lastKeyPressed = null;
	
	private Input() {}
	
	/**
	 * Gets the last key that was pressed.
	 * 
	 * @return	A {@code Key} representing the last key to be pressed during
	 * 			this 'frame', or {@code null} if no key has been
	 * 			pressed this 'frame'.
	 */
	public static Key getAnyKeyDown() {
		return lastKeyPressed;
	}
	
	/**
	 * Gets if a key was pressed for the first time after being released.
	 * 
	 * @param	key
	 * 			The key to check for.
	 * 
	 * @return	{@code true} if the key was pressed for the first time after being released,
	 * 			{@code false} otherwise.
	 * 
	 * @throws	NullPointerException
	 * 			If {@code key} is null.
	 */
	public static boolean getKeyDown(Key key) {
		if (firstPressedKeys[key.keyValue]) {
			if (!keysToChange.contains(key.keyValue)) {
				valuesToChange.add(true);
				keysToChange.add(key.keyValue);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Gets if a key is being held.
	 * 
	 * @param	key
	 * 			The key to check for.
	 * @return	{@code true} if the key is being held,
	 * 			{@code false} otherwise.
	 * 
	 * @throws	NullPointerException
	 * 			If {@code key} is null.
	 */
	public static boolean getKey(Key key) {
		return pressedKeys[key.keyValue];
	}
	
	/**
	 * Gets if a key was released.
	 * 
	 * @param	key
	 * 			The key to check for.
	 * 
	 * @return	{@code true} if they key was released,
	 * 			{@code false} otherwise.
	 * 
	 * @throws	NullPointerException
	 * 			If {@code key} is null.
	 */
	public static boolean getKeyUp(Key key) {
		if (releasedKeys[key.keyValue]) {
			if (!keysToChange.contains(key.keyValue)) {
				valuesToChange.add(false);
				keysToChange.add(key.keyValue);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Gets if a touch was pressed for the first time.
	 * 
	 * @return 	{@code true} if a touch was pressed for the first time,
	 * 			{@code false} otherwise.
	 */
	public static boolean isTouchDown() {
		for (Touch touch : touches) {
			if (touch.isFirstPressed())
				return true;
		}
		return false;
	}
	
	/**
	 * Gets if a touch is being held or moved.
	 *
	 * @return	{@code true} if a touch is being held,
	 * 			{@code false} otherwise.
	 */
	public static boolean isTouchHeld() {
		for (Touch touch : touches) {
			if (!touch.isReleased())
				return true;
		}
		return false;
	}
	
	/**
	 * Gets if a touchs was released.
	 *
	 * @return 	{@code true} if a touch was released,
	 * 			{@code false} otherwise.
	 */
	public static boolean getTouchUp(int button) {
		for (Touch touch : touches) {
			if (touch.isReleased())
				return true;
		}
		return false;
	}
	
	/**
	 * @return	{@code true} if the mouse was scrolled up,
	 * 			{@code false} otherwise.
	 */
	public boolean scrolledUp() {
		return scrollAmount == -1;
	}
	
	/**
	 * @return	{@code true} if the mouse was scrolled down,
	 * 			{@code false} otherwise.
	 */
	public boolean scrolledDown() {
		return scrollAmount == 1;
	}
	
	/** Must be called every 'frame' of the application so that it can reset the proper variables. */
	public static void update() {
		lastKeyPressed = null;
		scrollAmount = 0;
		
		for (int i = 0; i <keysToChange.size(); i++) {
			boolean firstPressed = valuesToChange.get(i);
			int key = keysToChange.get(i);
			if (firstPressed)
				firstPressedKeys[key] = false;
			else
				releasedKeys[key] = false;
		}
		keysToChange.clear();
		valuesToChange.clear();

		for (int i = 0; i < touches.size(); i++) {
			touches.get(i).update();
			if (touches.get(i).isReleased())
				touches.remove(i--);
		}
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean keyDown(int keycode) {
		lastKeyPressed = Key.getKey(keycode);
		this.updateKeys(keycode, true);
		return false;
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean keyUp(int keycode) {
		this.updateKeys(keycode, false);
		return false;
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean keyTyped(char character) { return false; }
	
	/** Helper method for key input. */
	private void updateKeys(int code, boolean pressed) {
		if (code < 255) {
			if (!pressedKeys[code])
				firstPressedKeys[code] = pressed;
			pressedKeys[code] = pressed;
			releasedKeys[code] = !pressed;
		}
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for (Touch touch : touches) {
			if (touch.pointer == pointer)
				return false;
		}

		touches.add(new Touch(screenX, screenY, pointer, button));
		return false;
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for (Touch touch : touches) {
			if (touch.pointer == pointer) {
				touch.release();
				break;
			}
		}
		return false;
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for (Touch touch : touches) {
			if (touch.pointer == pointer) {
				touch.move(screenX, screenY);
				break;
			}
		}
		return false;
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		//for computer mouse
		return false;
	}
	
	/** NOT FOR PUBLIC USE */
	@Override
	public boolean scrolled(int amount) {
		scrollAmount = amount;
		return false;
	}
	
}
