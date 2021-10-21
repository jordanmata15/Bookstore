package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PersistentInventory implements Inventory{
	
	private SimpleInventory decoratedInventory;
	private int commandsSoFar;
	final private String filePath;
	final private String commandPrefix = "command_";
	final private String mementoPrefix = "InventoryMemento";
	final private String serializedSuffix = ".ser";
	final private int actionsBeforeDump = 10;
	
	public PersistentInventory(SimpleInventory toDecorate, String logTo) throws ClassNotFoundException, IOException {
		this.filePath = logTo;
		this.decoratedInventory = toDecorate;
		this.commandsSoFar = 0;
		this.recoverFromBackup();
	}
	
	@Override
	public int addBook(Book toAdd) throws IOException {
		InventoryCommand command = new AddBookCommand(toAdd);
		int bookCount = (int) command.execute(decoratedInventory);
		this.handleAction(command);
		return bookCount;
	}

	@Override
	public int sellBook(Book toSell) throws IOException {
		InventoryCommand command = new SellBookCommand(toSell);
		int bookCount = (int) command.execute(decoratedInventory);
		this.handleAction(command);
		return bookCount;
	}

	@Override
	public double updatePrice(Book toUpdate) throws IOException {
		InventoryCommand command = new UpdatePriceCommand(toUpdate);
		double priceChanged = command.execute(decoratedInventory);
		this.handleAction(command);
		return priceChanged;
	}

	@Override
	public double getPriceByID(Integer toFind) {
		return this.decoratedInventory.getPriceByID(toFind);
	}
	
	@Override
	public double getPriceByTitle(String toFind) {
		return this.decoratedInventory.getPriceByTitle(toFind);
	}

	@Override
	public int getQuantityByID(Integer toFind) {
		return this.decoratedInventory.getQuantityByID(toFind);
	}
	
	@Override
	public int getQuantityByTitle(String toFind) {
		return this.decoratedInventory.getQuantityByTitle(toFind);
	}
	
	private void handleAction(InventoryCommand command) throws IOException {
		this.serializeCommand(command);
		this.commandsSoFar++;
		if (this.commandsSoFar >= this.actionsBeforeDump)
			this.dumpInventoryToFile();
	}
	
	
	private void dumpInventoryToFile() throws IOException {
		File mementoFile = new File(this.mementoPrefix + this.serializedSuffix);
		List<File> commandLogsAndInvMemento = this.getCommandFiles();
		commandLogsAndInvMemento.add(mementoFile);
		
		this.renameLogFiles(commandLogsAndInvMemento);
		this.writeOutMemento();
		this.clearLogFiles(commandLogsAndInvMemento);
		this.commandsSoFar = 0;
	}
	
	
	private void recoverFromBackup() throws ClassNotFoundException, IOException {
		this.readInMemento();
		this.replayLoggedCommands();
	}
	
	
	private boolean writeOutMemento() throws IOException {
		File mementoFile = new File(this.mementoPrefix + this.serializedSuffix);
		InventoryMemento currentState = new InventoryMemento();
		currentState.setState("TitleMap", this.decoratedInventory.getTitleMap());
		currentState.setState("IDMap", this.decoratedInventory.getIDMap());
		try {
			FileOutputStream outputStream = new FileOutputStream(mementoFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(currentState);
			objectStream.close();
			outputStream.close();
			return true;
		}
		catch (IOException e){ 
			throw e; 
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void readInMemento() throws IOException, ClassNotFoundException {
		File mementoFile = new File(this.mementoPrefix + this.serializedSuffix);
		if (!mementoFile.exists())
			return;
		try {
			FileInputStream outputStream = new FileInputStream(mementoFile);
			ObjectInputStream objectStream = new ObjectInputStream(outputStream);
			InventoryMemento invMemento = (InventoryMemento) objectStream.readObject();
			this.decoratedInventory.setTitleMap((Map<String, Integer>)invMemento.getState("TitleMap"));
			this.decoratedInventory.setIDMap((Map<Integer, Book>)invMemento.getState("IDMap"));
			objectStream.close();
			outputStream.close();
	}
		catch (IOException | ClassNotFoundException e){
			throw e;
		}
	}
	
	
	private void renameLogFiles(List<File> filesList) {
		for (File file:filesList) {
			File newName = new File("temp_"+file.getName());
			file.renameTo(newName);
		}
	}
	
	
	private void clearLogFiles(List<File> redundantFilesList) {
		for (File file:redundantFilesList) {
			file = new File(this.filePath + File.separator + "temp_" + file.getName());
			file.delete();			
		}
	}
	
	private void serializeCommand(InventoryCommand command) throws IOException {
		this.validatePreviousCommandNumber();
		String filePath = this.fullFilePath(this.commandPrefix 		+ 
											this.commandsSoFar 		+ 
											this.serializedSuffix);
		File nextLogFile = new File(filePath);
		try {
			FileOutputStream outputStream = new FileOutputStream(nextLogFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(command);
			objectStream.close();
			outputStream.close();
		} catch (IOException e) {
			throw e;
		}
	}
	
	private InventoryCommand deserializeCommand(int i) throws IOException, ClassNotFoundException {
		String filePath = this.fullFilePath(this.commandPrefix + i + this.serializedSuffix);
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
	
	private List<File> getCommandFiles() throws IOException {
		List<File> commandLogFiles = new ArrayList<File>();
		String commandLogRegex = this.commandPrefix + "\\d+\\" + this.serializedSuffix;
		try {
			commandLogFiles = Files.list(Paths.get(this.filePath))
							        .filter(Files::isRegularFile)
							        .map(Path::toFile)
							        .filter(file->Pattern.matches(commandLogRegex, file.getName()))
							        .collect(Collectors.toList());
		} catch (IOException e) {
			throw e;
		}
		return commandLogFiles;
	}
	
	private void replayLoggedCommands() throws ClassNotFoundException, IOException {
		while (this.existsCommandLogFile(this.commandsSoFar)) {
			InventoryCommand command = this.deserializeCommand(this.commandsSoFar);
			command.execute(this.decoratedInventory);
			this.commandsSoFar++;
		}
	}
	
	private void validatePreviousCommandNumber() {
		if (this.existsCommandLogFile(this.commandsSoFar)) {
			this.commandsSoFar++;
			this.validatePreviousCommandNumber();
		}
		if (this.commandsSoFar == 0 || this.existsCommandLogFile(this.commandsSoFar-1))
			return;
		else 
			this.commandsSoFar = 0;
	}
	
	private boolean existsCommandLogFile(int commandNumber) {
		String filePath = this.fullFilePath(this.commandPrefix 		+ 
											commandNumber 			+ 
											this.serializedSuffix);
		File commandFile = new File(filePath);
		return commandFile.exists();
	}
	
	private String fullFilePath(String filename) {
		return this.filePath.concat(File.separator+filename);
	}
}
