package main;

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
	public double execute(Inventory toExecuteOn) {
		return toExecuteOn.sellBook(toExecuteWith);
	}
}