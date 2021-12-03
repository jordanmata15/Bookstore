package main;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Command object used to sell a book from the inventory.
 * Serializable to allow command to be written and read from
 * disk.
 * @author Jordan
 */
public class SellBookCommand implements InventoryCommand{

	/** Base version */
	private static final long serialVersionUID = 1L;
	
	Book toExecuteWith;
	
	/**
	 * SellBookCommand Constructor.
	 * @param toSell the book object to sell from the inventory. The book should contain
	 * 			the ID of the book to sell and the quantity of the book to sell.
	 */
	public SellBookCommand(Book toSell) {
		this.toExecuteWith = toSell;
	}
	
	/**
	 * Execute the sell book command on the passed in inventory.
	 * @return the number of books remaining in the inventory.
	 * @throws NoSuchElementException if the book ID is not in the inventory or if there
	 * 			are not enough copies to sell.
	 * @throws IOException if the command fails to be serialized.
	 */
	@Override
	public double execute(SimpleInventory toExecuteOn) throws NoSuchElementException, IOException {
		return toExecuteOn.sellBook(toExecuteWith);
	}
}