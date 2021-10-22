package main;

import java.io.IOException;

/**
 * Command object used to add a book to the inventory.
 * Serializable to allow command to be written and read from
 * disk.
 * @author Jordan
 */
public class AddBookCommand implements InventoryCommand{

	/** Base version */
	private static final long serialVersionUID = 1L;
	
	Book toExecuteWith;
	
	/**
	 * AddBookCommand constructor.
	 * @param toAdd the book object to add to the inventory. The book should contain
	 * 			the ID of the book to add and the quantity of the book to add.
	 */
	public AddBookCommand(Book toAdd) {
		this.toExecuteWith = toAdd;
	}
	
	/**
	 * Add this particular book to the inventory.
	 * @param toExectuteOn inventory object to call the command from.
	 * @return The number of copies of this book in the inventory after adding.
	 * @throws IOException if the command fails to be serialized.
	 */
	@Override
	public double execute(SimpleInventory toExecuteOn) throws IOException {
		return toExecuteOn.addBook(toExecuteWith);
	}
}