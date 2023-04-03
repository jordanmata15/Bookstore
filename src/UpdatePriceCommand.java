package main;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Command object used to update the price of a book from the inventory.
 * Serializable to allow command to be written and read from
 * disk.
 * @author Jordan
 */
public class UpdatePriceCommand implements InventoryCommand{
	
	/** Base version */
	private static final long serialVersionUID = 1L;
	
	private Book toExecuteWith;
	
	/**
	 * UpdatePriceCommand constructor.
	 * @param toUpdate the book object to update the price of in the inventory. 
	 * 			The book should contain the ID of the book to update the price of 
	 * 			and the new price of the book.
	 */
	public UpdatePriceCommand(Book toUpdate){
		this.toExecuteWith = toUpdate;
	}
	
	/**
	 * Update the price of this particular book in the inventory.
	 * @param toExectuteOn inventory object to call the command from.
	 * @return The new price of this book in the inventory after updating.
	 * @throws NoSuchElementException if the book ID is not in the inventory.
	 * @throws IOException if the command fails to be serialized.
	 */
	@Override
	public double execute(Inventory toExecuteOn) throws NoSuchElementException, IOException {
		return toExecuteOn.updatePrice(toExecuteWith);
	}
}
