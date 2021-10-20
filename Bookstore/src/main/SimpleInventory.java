package main;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class SimpleInventory implements Inventory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, Integer> titleToIDMap;
	private Map<Integer, Book> idToBookMap;
	
	public SimpleInventory() {
		this.titleToIDMap = new HashMap<String, Integer>();
		this.idToBookMap = new HashMap<Integer, Book>();
	}
	
	@Override
	public int addBook(Book toAdd) {
		if (this.existsBookTitle(toAdd.getTitle()))
			return this.addExistingBook(toAdd);
		else
			return this.addNewBook(toAdd);
	}
	
	private int addNewBook(Book toAdd) {
		Book toAddCopy = new Book(toAdd.getTitle(), 
									toAdd.getID(), 
									toAdd.getQuantity(), 
									toAdd.getPrice());
		this.titleToIDMap.put(toAdd.getTitle(), toAdd.getID());
		this.idToBookMap.put(toAdd.getID(), toAddCopy);
		return toAdd.getQuantity();
	}
	
	private int addExistingBook(Book toAdd) {
		Integer uniqueID = toAdd.getID();
		Book bookReferenceToAddTo = this.idToBookMap.get(uniqueID);
		bookReferenceToAddTo.incrementQuantity(toAdd.getQuantity());
		return bookReferenceToAddTo.getQuantity();
	}
	
	@Override
	public int sellBook(Book toSell) throws NoSuchElementException{
		if (!this.existsBook(toSell) || this.getQuantityByID(toSell.getID()) <= 0)
			throw new NoSuchElementException();
		Book bookReferenceToSellFrom = this.idToBookMap.get(toSell.getID());
		bookReferenceToSellFrom.incrementQuantity(-toSell.getQuantity());
		return bookReferenceToSellFrom.getQuantity();
	}
	
	@Override
	public double updatePrice(Book toUpdate) {
		if (!this.existsBook(toUpdate))
			throw new NoSuchElementException();
		Book bookReferenceToUpdate = this.idToBookMap.get(toUpdate.getID());
		bookReferenceToUpdate.setPrice(toUpdate.getPrice());
		return bookReferenceToUpdate.getPrice();
	}
	
	@Override
	public int getQuantityByTitle(String toFind) throws NoSuchElementException{
		Book bookToFind = getBookByTitle(toFind);
		return bookToFind.getQuantity();
	}
	
	@Override
	public int getQuantityByID(Integer toFind) throws NoSuchElementException{
		Book bookToFind = getBookByID(toFind);
		return bookToFind.getQuantity();
	}
	
	@Override
	public double getPriceByTitle(String toFind) throws NoSuchElementException{
		Book bookToFind = getBookByTitle(toFind);
		return bookToFind.getPrice();
	}
	
	@Override
	public double getPriceByID(Integer toFind) throws NoSuchElementException{
		Book bookToFind = getBookByID(toFind);
		return bookToFind.getPrice();
	}
	
	private Book getBookByTitle(String toFind) {
		if (!this.existsBookTitle(toFind))
			throw new NoSuchElementException();
		Integer bookUniqueID = this.titleToIDMap.get(toFind);
		Book bookReferenceToFind = this.idToBookMap.get(bookUniqueID);
		return bookReferenceToFind;
	}
	
	private Book getBookByID(Integer toFind) {
		if (!this.existsBookID(toFind))
			throw new NoSuchElementException();
		Book bookReferenceToFind = this.idToBookMap.get(toFind);
		return bookReferenceToFind;
	}
	
	private boolean existsBook(Book toCheck) {
		return this.existsBookID(toCheck.getID());
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
	
	public String toString() {
		return String.valueOf(this.idToBookMap.size());
	}
}