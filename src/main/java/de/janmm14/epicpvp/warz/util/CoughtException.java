package de.janmm14.epicpvp.warz.util;

/**
 * Simple utility class indicating that it's cause is caught and handled, just one part does not work
 */
public class CoughtException extends Exception {

	public CoughtException(Throwable cause) {
		super( cause );
	}
}
