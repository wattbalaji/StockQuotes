package com.trade.exception;

/**
 * Exception representing that a quote symbol not  found.
 * @author Balaji Wattamwar
 *
 */
public class SymbolNotFoundException extends Exception {

	public SymbolNotFoundException(String message ) {
		super(message);
	}
}
