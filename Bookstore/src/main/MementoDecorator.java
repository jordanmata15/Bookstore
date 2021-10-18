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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MementoDecorator implements Inventory{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Inventory decoratedInventory;
	int actionsCount;
	final String filePath;
	final String fileName = "InventoryMemento.ser";
	String fullMementoPath;
	final int actionsBeforeDump = 10;
	
	public MementoDecorator(Inventory inv, String logTo) {
		this.filePath = logTo;
		this.fullMementoPath = this.filePath + File.separator + this.fileName;
		this.decoratedInventory = inv;
		this.actionsCount = 0;
		this.recoverFromBackup();
	}
	
	@Override
	public int addBook(Book toAdd) {
		int bookCount = this.decoratedInventory.addBook(toAdd);
		this.handleAction();
		return bookCount;
	}

	@Override
	public int sellBook(Book toSell) {
		int bookCount = this.decoratedInventory.sellBook(toSell);
		this.handleAction();
		return bookCount;
	}

	@Override
	public double updatePrice(Book toUpdate) {
		double priceChanged = this.decoratedInventory.updatePrice(toUpdate);
		this.handleAction();
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
	
	private void handleAction() {
		this.actionsCount++;
		if (this.actionsCount >= this.actionsBeforeDump) {
			this.dumpInventoryToFile();
		}
	}
	
	
	private void dumpInventoryToFile() {
		File mementoFile = new File(this.fullMementoPath);
		List<File> commandLogsAndInvMemento = this.getLogFiles();
		commandLogsAndInvMemento.add(mementoFile);
		
		this.renameRedundantFiles(commandLogsAndInvMemento);
		this.writeOut();
		this.clearRedundantFiles(commandLogsAndInvMemento);
		this.actionsCount = 0;
	}
	
	
	private boolean writeOut() {
		File mementoFile = new File(this.fullMementoPath);
		InventoryMemento currentState = new InventoryMemento(decoratedInventory);
		try {
			FileOutputStream outputStream = new FileOutputStream(mementoFile);
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(currentState);
			objectStream.close();
			outputStream.close();
			return true;
		}
		catch (Exception e){ return false; }
	}
	
	
	private void recoverFromBackup() {
		File mementoFile = new File(this.fullMementoPath);
		if (!mementoFile.exists())
			return;
		try {
			FileInputStream outputStream = new FileInputStream(mementoFile);
			ObjectInputStream objectStream = new ObjectInputStream(outputStream);
			InventoryMemento inventoryMemento = (InventoryMemento) objectStream.readObject();
			this.decoratedInventory = inventoryMemento.getState();
			objectStream.close();
			outputStream.close();
		}
		catch (Exception e){ e.printStackTrace(); }
	}
	
	
	private void renameRedundantFiles(List<File> filesList) {
		filesList.forEach(file->{
							File newName = new File("temp_"+file.getName());
							file.renameTo(newName);
							});
	}
	
	
	private void clearRedundantFiles(List<File> redundantFilesList) {
		redundantFilesList.forEach(file->{
										file = new File(this.filePath+File.separator+"temp_"+file.getName());
										file.delete();	
										});
	}
	
	
	private List<File> getLogFiles() {
		List<File> commandLogFiles = new ArrayList<File>();
		String commandLogRegex = ".?command_\\d+\\.ser";
		try {
			commandLogFiles = Files.list(Paths.get(this.filePath))
							        .filter(Files::isRegularFile)
							        .map(Path::toFile)
							        .filter(file->Pattern.matches(commandLogRegex, file.getName()))
							        .collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return commandLogFiles;
	}
}