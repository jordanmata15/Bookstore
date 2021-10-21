package main;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Command object used to add an object to the inventory.
 * Serializable to allow command to be written and read from
 * disk.
 * @author Jordan
 *
 */
public class AddBookCommand extends InventoryCommand{

	/**
	 * Base version
	 */
	private static final long serialVersionUID = 1L;
	
	Book toExecuteWith;
	
	/**
	 * Simple constructor.
	 * @param toAdd the book object to add to the inventory.
	 */
	public AddBookCommand(Book toAdd) {
		this.toExecuteWith = toAdd;
	}
	
	/**
	 * TODO make inventory a simpleInventory
	 * Add this particular book to the inventory.
	 * @param toExectuteOn inventory object to call the command from.
	 * @return The number of copies of this book in the inventory after adding.
	 * @throws IOException if the command fails to be serialized.
	 */
	@Override
	public double execute(Inventory toExecuteOn) throws IOException {
		return toExecuteOn.addBook(toExecuteWith);
	}
}