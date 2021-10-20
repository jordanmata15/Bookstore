package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;

public class CommandDecorator implements Inventory{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Inventory decoratedInventory;
	String folderName;
	int commandsSoFar;
	
	public CommandDecorator(Inventory toDecorate, String logTo) throws ClassNotFoundException,
																		IOException {
		this.decoratedInventory = toDecorate;
		this.folderName = logTo;
		this.commandsSoFar = 0;
		this.replayLoggedCommands();
	}
	
	@Override
	public int addBook(Book toAdd) throws NoSuchElementException, IOException {
		InventoryCommand command = new AddBookCommand(toAdd);
		this.serialize(command);
		return (int) command.execute(decoratedInventory);
	}

	@Override
	public int sellBook(Book toSell) throws IOException {
		InventoryCommand command = new SellBookCommand(toSell);
		this.serialize(command);
		int bookCount;
		try{
			bookCount = (int) command.execute(decoratedInventory);
		} catch(NoSuchElementException e) {
			this.deleteLastSerializedCommand();
			throw e;
		}
		return bookCount;
	}

	@Override
	public double updatePrice(Book toUpdate) throws IOException {
		InventoryCommand command = new UpdatePriceCommand(toUpdate);
		this.serialize(command);
		double bookPrice;
		try{
			bookPrice = (int) command.execute(decoratedInventory);
		} catch(NoSuchElementException e) {
			this.deleteLastSerializedCommand();
			throw e;
		}
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

	private void serialize(InventoryCommand command) throws IOException {
		this.validatePreviousCommandNumber();
		String filePath = this.fileCompletePath("command_" + (this.commandsSoFar) + ".ser");
		File nextLogFile = new File(filePath);
		try {
			FileOutputStream outputStream = new FileOutputStream(nextLogFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(command);
			objectStream.close();
			outputStream.close();
			this.commandsSoFar++;
		} catch (IOException e) {
			throw e;
		}
	}
	
	private InventoryCommand deserialize(int i) throws IOException, ClassNotFoundException {
		String filePath = this.fileCompletePath("command_" + i + ".ser");
		File nextLogFile = new File(filePath);
		try {
			FileInputStream inputStream = new FileInputStream(nextLogFile);
			ObjectInputStream objectStream = new ObjectInputStream(inputStream);
			InventoryCommand command = (InventoryCommand) objectStream.readObject();
			objectStream.close();
			inputStream.close();
			return command;
		} catch (IOException | ClassNotFoundException e) {
			throw e;
		}
	}
	
	private void validatePreviousCommandNumber() {
		if (this.commandsSoFar == 0 || this.existsCommandLogFile(this.commandsSoFar-1))
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
	
	private void replayLoggedCommands() throws ClassNotFoundException, IOException {
		while (this.existsCommandLogFile(this.commandsSoFar)) {
			InventoryCommand command = this.deserialize(this.commandsSoFar);
			command.execute(this.decoratedInventory);
			this.commandsSoFar++;
		}
	}
	
	private void deleteLastSerializedCommand() {
		this.commandsSoFar--;
		String filePath = this.fileCompletePath("command_" + (this.commandsSoFar) + ".ser");
		File toDelete = new File(filePath);
		toDelete.delete();
	}
}