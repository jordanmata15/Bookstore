package main;

import java.io.IOException;
import java.util.NoSuchElementException;

public class UpdatePriceCommand extends InventoryCommand{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Book toExecuteWith;
	
	public UpdatePriceCommand(Book toUpdate){
		this.toExecuteWith = toUpdate;
	}
	
	@Override
	public double execute(SimpleInventory toExecuteOn) throws NoSuchElementException, IOException {
		return toExecuteOn.updatePrice(toExecuteWith);
	}
}