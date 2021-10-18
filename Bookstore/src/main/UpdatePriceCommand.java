package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

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
		File someFile = null;
		this.appendCommand(someFile);
		return toExecuteOn.updatePrice(toExecuteWith);
	}
	
	private void appendCommand(File filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename, true);
			ObjectOutputStream objOutput = new ObjectOutputStream(fileOut);
			objOutput.writeObject(this);
			objOutput.close();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}