package main;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class Inventory{

	private Map<String, Integer> titleToIDMap;
	private Map<Integer, Book> idToBookMap;
	private int uniqueBookCount;
	
	public Inventory() {
		this.titleToIDMap = new HashMap<String, Integer>();
		this.idToBookMap = new HashMap<Integer, Book>();
		this.uniqueBookCount = 0;
	}
	
	
	public int addTitle(String title, int quantity, double price) {
		if (this.existsBookTitle(title))
			return this.addExistingTitle(title, quantity, price);
		else
			return this.addNewTitle(title, quantity, price);
	}
	
	private int addNewTitle(String title, int quantity, double price) {
		Integer uniqueID = this.uniqueBookCount;
		Book bookToAdd = new Book(title, uniqueID, quantity, price);
		this.titleToIDMap.put(bookToAdd.getTitle(), bookToAdd.getID());
		this.idToBookMap.put(bookToAdd.getID(), bookToAdd);
		this.uniqueBookCount++;
		return bookToAdd.getQuantity();
	}
	
	private int addExistingTitle(String title, int quantity, double price) {
		Integer uniqueID = getIDAndTitle(title).getValue();
		Book bookToAdd = this.idToBookMap.get(uniqueID);
		bookToAdd.addQuantity(quantity);
		bookToAdd.setPrice(price);
		return bookToAdd.getQuantity();
	}
	
	public int sell(Book existingBook) {
		return -1;
	}
	
	
	public int updatePrice(double newPrice) {
		return -1;
	}
	
	public int inStockCount(String identifier) {
		return 0;
	}
	
	public double getPrice(String identifier) {
		return 0.0;
	}
	
	private AbstractMap.SimpleEntry<String, Integer> getIDAndTitle(String identifier) {
		Integer uniqueID;
		String title;
		try {
			uniqueID = Integer.parseInt(identifier);
			title = "";
		}
		catch(NumberFormatException e) {
			/* the identifier is not strictly numeric */
			uniqueID = -1;
			title = identifier;
		}
		return new AbstractMap.SimpleEntry<String, Integer>(title, uniqueID);
	}
	
	private boolean existsBookID(Integer id) {
		if (this.idToBookMap.containsKey(id))
			return true;
		else
			return false;
	}
	
	private boolean existsBookTitle(String title) {
		if (this.titleToIDMap.containsKey(title))
			return true;
		else
			return false;
	}
}