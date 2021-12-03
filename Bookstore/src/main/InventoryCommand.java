package main;

import java.io.IOException;
import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * Command object interface for actions that change the inventory state.
 * @author Jordan
 */
public interface InventoryCommand extends Serializable{

	/** Base version */
	static final long serialVersionUID = 1L;
	
	/**
	 * Perform a command on a particular book in the inventory.
	 * @param toExectuteOn inventory object to call the command from.
	 * @return Quantity of this book remaining in inventory after this action if adding or selling.
	 * 			Otherwise, the price of the book after updating the price.
	 * @throws NoSuchElementException if the book ID is not in the inventory. Or if
	 * 			attempting to sell and insufficient amount to sell.
	 * @throws IOException if the command fails to be serialized.
	 */
	public abstract double execute(Inventory decoratedInventory) throws NoSuchElementException, IOException;
}