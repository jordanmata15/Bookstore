package main;

import java.io.IOException;

public class AddBookCommand extends InventoryCommand{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Book toExecuteWith;
	
	public AddBookCommand(Book toAdd) {
		this.toExecuteWith = toAdd;
	}
	
	@Override
	public double execute(Inventory toExecuteOn) throws IOException {
		return toExecuteOn.addBook(toExecuteWith);
	}
}