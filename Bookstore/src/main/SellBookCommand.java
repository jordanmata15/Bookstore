package main;

import java.io.IOException;
import java.util.NoSuchElementException;

public class SellBookCommand extends InventoryCommand{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Book toExecuteWith;
	
	public SellBookCommand(Book toSell) {
		this.toExecuteWith = toSell;
	}
	
	@Override
	public double execute(SimpleInventory toExecuteOn) throws NoSuchElementException, IOException {
		return toExecuteOn.sellBook(toExecuteWith);
	}
}