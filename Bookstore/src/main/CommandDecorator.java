package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CommandDecorator implements Inventory{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Inventory decoratedInventory;
	String folderName;
	int commandsSoFar;
	
	public CommandDecorator(Inventory toDecorate, String logTo) {
		this.decoratedInventory = toDecorate;
		this.folderName = logTo;
		this.commandsSoFar = 0;
		this.replayLoggedCommands();
	}
	
	@Override
	public int addBook(Book toAdd) {
		InventoryCommand command = new AddBookCommand(toAdd);
		int bookCount = (int) command.execute(decoratedInventory);
		this.serialize(command);
		return bookCount;
	}

	@Override
	public int sellBook(Book toSell) {
		InventoryCommand command = new SellBookCommand(toSell);
		int bookCount = (int) command.execute(decoratedInventory);
		this.serialize(command);
		return bookCount;
	}

	@Override
	public double updatePrice(Book toUpdate) {
		InventoryCommand command = new UpdatePriceCommand(toUpdate);
		double bookPrice = command.execute(decoratedInventory);
		this.serialize(command);
		return bookPrice;
	}

	@Override
	public int getQuantityByTitle(String toFind) {
		return this.decoratedInventory.getQuantityByTitle(toFind);
	}

	@Override
	public int getQuantityByID(Integer toFind) {
		return this.decoratedInventory.getQuantityByID(toFind);
	}

	@Override
	public double getPriceByTitle(String toFind) {
		return this.decoratedInventory.getPriceByTitle(toFind);
	}

	@Override
	public double getPriceByID(Integer toFind) {
		return this.decoratedInventory.getPriceByID(toFind);
	}

	private void serialize(InventoryCommand command) {
		this.validatePreviousCommandNumber();
		String filePath = this.fileCompletePath("command_" + (this.commandsSoFar) + ".ser");
		File nextLogFile = new File(filePath);
		System.out.println("Creating command" + filePath);
		try {
			FileOutputStream outputStream = new FileOutputStream(nextLogFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(command);
			objectStream.close();
			outputStream.close();
		} catch (Exception e) {System.out.println("ERRORORORORR" /*TODO*/ );}
		this.commandsSoFar++;
	}
	
	private InventoryCommand deserialize(int i) {
		String filePath = this.fileCompletePath("command_" + i + ".ser");
		File nextLogFile = new File(filePath);
		try {
			FileInputStream inputStream = new FileInputStream(nextLogFile);
			ObjectInputStream objectStream = new ObjectInputStream(inputStream);
			InventoryCommand command = (InventoryCommand) objectStream.readObject();
			objectStream.close();
			inputStream.close();
			return command;
		} catch (Exception e) { }
		this.commandsSoFar++;
		return null; // TODO null check? Replace with blank command?
	}
	
	private void validatePreviousCommandNumber() {
		if (this.commandsSoFar == 0)
			return;
		if (this.existsCommandLogFile(this.commandsSoFar-1))
			return;
		else 
			this.commandsSoFar = 0;
	}
	
	private String fileCompletePath(String filename) {
		return this.folderName.concat(File.separator+filename);
	}
	
	private boolean existsCommandLogFile(int commandNumber) {
		String filePath = this.fileCompletePath("command_" + (commandNumber) + ".ser");
		File commandFile = new File(filePath);
		return commandFile.exists();
	}
	
	private void replayLoggedCommands() {
		while (this.existsCommandLogFile(this.commandsSoFar)) {
			InventoryCommand command = this.deserialize(this.commandsSoFar);
			command.execute(decoratedInventory);
			this.commandsSoFar++;
		}
	}
}