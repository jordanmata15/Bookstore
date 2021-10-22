package main;

import java.io.IOException;
import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class InventoryCommand implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract double execute(SimpleInventory toExecuteOn) throws NoSuchElementException, IOException;
}