package main;

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
	public double execute(Inventory toExecuteOn) {
		return toExecuteOn.updatePrice(toExecuteWith);
	}
}