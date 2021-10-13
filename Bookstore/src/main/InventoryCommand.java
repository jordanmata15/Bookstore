package main;

public interface InventoryCommand{
	public void execute(Book toAdd);
	public void undo();
}

class AddBookCommand implements InventoryCommand{
	private Inventory inventory;
	
	public AddBookCommand(Inventory inv) {
		this.inventory = inv;
	}
	
	public void execute(Book toAdd) {
	}
	
	public void undo() {
	}
	
	public void store() {
	}
}
