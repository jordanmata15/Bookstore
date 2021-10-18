package main;

import java.io.Serializable;

public abstract class InventoryCommand implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract double execute(Inventory toExecuteOn);
}