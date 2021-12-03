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

/**
 * Inventory decorator that serializes commands that change the internal state of the 
 * inventory as they are executed. Serializes the entire inventory after a given number
 * of commands.
 * @author Jordan
 *
 */
public class PersistentInventory implements Inventory{
	
	final private String filePath;
	final private String commandPrefix = "command_";
	final private String mementoPrefix = "InventoryMemento";
	final private String serializedSuffix = ".ser";
	final private int actionsBeforeDump = 10;
	
	private Inventory decoratedInventory;
	private int commandsSoFar;
	
	/**
	 * PersistentInventory constructor. Attempts to restore the state of the inventory
	 * serialized in the provided file path. Reads in the following files:
	 * 
	 * command_#.ser 	(# is any integer greater than or equal to 0)
	 * InventoryMemento.ser
	 * 
	 * @param toDecorate	Inventory object to decorate.
	 * @param logTo			File path containing any serialized commands/inventories to restore.
	 * 						Will also write new commands/inventory mementos to this folder.
	 * @throws ClassNotFoundException if the command/memento read in is not the correct class.
	 * @throws IOException if there is an error reading or writing to the serialized files.
	 */
	public PersistentInventory(Inventory toDecorate, String logTo) throws ClassNotFoundException, 
																				IOException {
		this.filePath = logTo;
		this.decoratedInventory = toDecorate;
		this.commandsSoFar = 0;
		this.recoverFromBackup();
	}
	
	/**
	 * Performs an "addBook" command on the decorated inventory and serializes the command
	 * along with the book to add for later restoration.
	 * @return the return of addBook.
	 * @throws IOException 	if the command fails to be serialized or if the memento
	 * 						fails to be written due to IO issues. 
	 */
	@Override
	public int addBook(Book toAdd) throws IOException {
		InventoryCommand command = new AddBookCommand(toAdd);
		int bookCount = (int) command.execute(decoratedInventory);
		this.handleAction(command);
		return bookCount;
	}

	/**
	 * Performs a "sellBook" command on the decorated inventory and serializes the command
	 * along with the book to sell for later restoration.
	 * @return the return of sellBook.
	 * @throws IOException 	if the command fails to be serialized or if the memento
	 * 						fails to be written due to IO issues. 
	 */
	@Override
	public int sellBook(Book toSell) throws IOException {
		InventoryCommand command = new SellBookCommand(toSell);
		int bookCount = (int) command.execute(decoratedInventory);
		this.handleAction(command);
		return bookCount;
	}

	/**
	 * Performs a "updatePrice" command on the decorated inventory and serializes the command
	 * along with the book to update for later restoration.
	 * @return the return of updatePrice.
	 * @throws IOException 	if the command fails to be serialized or if the memento
	 * 						fails to be written due to IO issues. 
	 */
	@Override
	public double updatePrice(Book toUpdate) throws IOException {
		InventoryCommand command = new UpdatePriceCommand(toUpdate);
		double priceChanged = command.execute(decoratedInventory);
		this.handleAction(command);
		return priceChanged;
	}

	/**
	 * Calls "getPriceByID" on the decorated inventory.
	 * @return the return of getPriceByID
	 */
	@Override
	public double getPriceByID(Integer toFind) {
		return this.decoratedInventory.getPriceByID(toFind);
	}
	
	/**
	 * Calls "getPriceByTitle" on the decorated inventory.
	 * @return the return of getPriceByTitle
	 */
	@Override
	public double getPriceByTitle(String toFind) {
		return this.decoratedInventory.getPriceByTitle(toFind);
	}

	/**
	 * Calls "getQuantityByID" on the decorated inventory.
	 * @return the return of getQuantityByID
	 */
	@Override
	public int getQuantityByID(Integer toFind) {
		return this.decoratedInventory.getQuantityByID(toFind);
	}
	
	/**
	 * Calls "getQuantityByTitle" on the decorated inventory.
	 * @return the return of getQuantityByTitle
	 */
	@Override
	public int getQuantityByTitle(String toFind) {
		return this.decoratedInventory.getQuantityByTitle(toFind);
	}
	
	/**
	 * Used to handle the events following a command that changes the internal
	 * state of the inventory. This includes serializing the command and writing
	 * out a memento if we've reached the limit of number of commands before doing so.
	 * @param command	Command object to serialize.
	 * @throws IOException	if we fail to serialize the command or the inventory memento.
	 */
	private void handleAction(InventoryCommand command) throws IOException {
		this.serializeCommand(command);
		this.commandsSoFar++;
		if (this.commandsSoFar >= this.actionsBeforeDump)
			this.dumpInventoryToDisk();
	}
	
	
	/**
	 * Handles renaming the serialized files, writing out the memento, and only then
	 * deleting the old serialized commands/mementos.
	 * @throws IOException 	if renaming the serialized files, writing out the memento, or
	 * 						deleting the old serialized files fails due to an IO issue..
	 */
	private void dumpInventoryToDisk() throws IOException {
		String mementoFileName = this.fullFilePath(this.mementoPrefix + this.serializedSuffix);
		File mementoFile = new File(mementoFileName);
		List<File> commandLogsAndInvMemento = this.getCommandFiles();
		commandLogsAndInvMemento.add(mementoFile);
		this.renameLogFiles(commandLogsAndInvMemento);
		this.serializeInventory();
		this.clearLogFiles(commandLogsAndInvMemento);
		this.commandsSoFar = 0;
	}
	
	/**
	 * Used to first read in the memento saved on disk, then play all the serialized 
	 * commands in order.
	 * @throws ClassNotFoundException 	if the deserialized commands or memento are not 
	 * 									the correct class.
	 * @throws IOException	if there is an issue reading the serialized command or memento
	 * 						related to IO.
	 */
	private void recoverFromBackup() throws ClassNotFoundException, IOException {
		this.deserializeInventory();
		this.replayLoggedCommands();
	}
	
	/**
	 * Used to serialize the inventory memento for later restoration.
	 * @throws IOException	if there was an issue writing the memento related to IO issues.
	 */
	private void serializeInventory() throws IOException {
		String fileName = this.fullFilePath(this.mementoPrefix + this.serializedSuffix);
		File mementoFile = new File(fileName);
		InventoryMemento currentState = this.decoratedInventory.saveState();
		try {
			FileOutputStream outputStream = new FileOutputStream(mementoFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(currentState);
			objectStream.close();
			outputStream.close();
		}
		catch (IOException e){ 
			throw e; 
		}
	}
	
	/**
	 * Used to deserialize (restore) the inventory memento and return this decorated
	 * inventory to a different state.
	 * @throws IOException	if there was an issue reading the memento related to IO issues.
	 * @throws ClassNotFoundException	if the read in file is not an InventoryMemento object.
	 */
	private void deserializeInventory() throws IOException, ClassNotFoundException {
		String fileName = this.fullFilePath(this.mementoPrefix + this.serializedSuffix);
		File mementoFile = new File(fileName);
		if (!mementoFile.exists())
			return;
		try {
			FileInputStream outputStream = new FileInputStream(mementoFile);
			ObjectInputStream objectStream = new ObjectInputStream(outputStream);
			InventoryMemento savedState = (InventoryMemento) objectStream.readObject();
			this.decoratedInventory.restoreState(savedState);
			objectStream.close();
			outputStream.close();
		}
		catch (IOException | ClassNotFoundException e){
			throw e;
		}
	}
	
	/**
	 * Renames the files on disk indicated in filesList by prepending with "temp_"
	 * @param filesList	List of files to rename.
	 */
	private void renameLogFiles(List<File> filesList) {
		filesList.forEach(file->{
			String newFileName = this.fullFilePath("temp_"+file.getName());
			File newFile = new File(newFileName);
			file.renameTo(newFile);
		});
	}
	
	/**
	 * Deletes the files on disk indicated in filesList if they exist after prepending "temp_".
	 * eg. command_1.ser may exist in the list and on disk. If temp_command_1.ser exists on disk,
	 * only temp_command_1.ser is deleted.
	 * @param filesList	List of files to delete.
	 */
	private void clearLogFiles(List<File> redundantFilesList) {
		redundantFilesList.forEach(file->{
			String newFileName = this.fullFilePath("temp_"+file.getName());
			File tempFile = new File(newFileName);
			tempFile.delete();
			});
	}
	
	/**
	 * Used to serialize the command object for later restoration.
	 * @param command	command object to serialize.
	 * @throws IOException	if there was an issue writing the command related to IO issues.
	 */
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
	
	/**
	 * Used to deserialize (restore) the command for replaying.
	 * @param i	the command number to restore.
	 * @return InventoryCommand command held in the serialized object
	 * @throws IOException	if there was an issue reading the command related to IO issues.
	 * @throws ClassNotFoundException	if the read in file is not an InventoryCommand object.
	 */
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
	
	/**
	 * Used to create a list of the command#.ser files in the filepath.
	 * @return List of files of serialized command objects
	 * @throws IOException if there is an IO issue related to reading the serialized file.
	 */
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
	
	/**
	 * Replays serialized commands in order and updates the number of commands in our
	 * PersistentInventory to enable creating a memento at the correct time.
	 * @throws ClassNotFoundException	if the serialized command is not an InventoryCommand.
	 * @throws IOException	if there are IO issues related to reading a command.
	 */
	private void replayLoggedCommands() throws ClassNotFoundException, IOException {
		while (this.existsCommandLogFile(this.commandsSoFar)) {
			InventoryCommand command = this.deserializeCommand(this.commandsSoFar);
			command.execute(this.decoratedInventory);
			this.commandsSoFar++;
		}
	}
	
	/**
	 * Validates the command count field by ensuring the previous serialized command
	 * exists on disk. If not, finds sets the command count field to the earliest 
	 * valid command number (starting at 0).
	 */
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
	
	/**
	 * Check if a serialized command file exists on disk.
	 * @param commandNumber	the command number to check for.
	 * @return	true if it exists on disk, false otherwise.
	 */
	private boolean existsCommandLogFile(int commandNumber) {
		String filePath = this.fullFilePath(this.commandPrefix 		+ 
											commandNumber 			+ 
											this.serializedSuffix);
		File commandFile = new File(filePath);
		return commandFile.exists();
	}
	
	/**
	 * Builds the full file path to a given filename and the filePath field.
	 * @param filename	file to build the full path for.
	 * @return	full path of the file.
	 */
	private String fullFilePath(String filename) {
		return this.filePath.concat(File.separator+filename);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InventoryMemento saveState() {
		return this.decoratedInventory.saveState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void restoreState(InventoryMemento stateToRestore) {
		this.decoratedInventory.restoreState(stateToRestore);
	}
}
