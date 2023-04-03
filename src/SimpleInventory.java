package main;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Inventory object with basic add, sell, update price, and lookup actions.
 * @author Jordan
 *
 */
public class SimpleInventory implements Inventory{
	
	private Map<String, Integer> titleToIDMap;
	private Map<Integer, Book> idToBookMap;
	
	/**
	 * SimpleInventory Constructor.
	 */
	public SimpleInventory() {
		this.titleToIDMap = new HashMap<String, Integer>();
		this.idToBookMap = new HashMap<Integer, Book>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int addBook(Book toAdd) {
		if (this.existsBookTitle(toAdd.getTitle()))
			return this.addExistingBook(toAdd);
		else
			return this.addNewBook(toAdd);
	}
	
	/**
	 * Adds the book to our intenal maps for easy lookup later.
	 * @param toAdd	The book to add to our database.
	 * @return number of copies of this book after adding.
	 */
	private int addNewBook(Book toAdd) {
		Book toAddCopy = new Book(toAdd.getTitle(), 
									toAdd.getID(), 
									toAdd.getQuantity(), 
									toAdd.getPrice());
		this.titleToIDMap.put(toAdd.getTitle(), toAdd.getID());
		this.idToBookMap.put(toAdd.getID(), toAddCopy);
		return toAdd.getQuantity();
	}
	
	/**
	 * Increment the quantity of the book with this unique ID.
	 * @param toAdd	Book object with the unique ID and quantity we need. Other 
	 * 				fields are ignored.
	 * @return total number of copies after the addition.
	 */
	private int addExistingBook(Book toAdd) {
		Integer uniqueID = toAdd.getID();
		Book bookReferenceToAddTo = this.idToBookMap.get(uniqueID);
		bookReferenceToAddTo.incrementQuantity(toAdd.getQuantity());
		return bookReferenceToAddTo.getQuantity();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int sellBook(Book toSell) throws NoSuchElementException{
		if (!this.existsBook(toSell) || this.getQuantityByID(toSell.getID()) <= 0)
			throw new NoSuchElementException();
		Book bookReferenceToSellFrom = this.idToBookMap.get(toSell.getID());
		bookReferenceToSellFrom.incrementQuantity(-toSell.getQuantity());
		return bookReferenceToSellFrom.getQuantity();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double updatePrice(Book toUpdate) throws NoSuchElementException{
		if (!this.existsBook(toUpdate))
			throw new NoSuchElementException();
		Book bookReferenceToUpdate = this.idToBookMap.get(toUpdate.getID());
		bookReferenceToUpdate.setPrice(toUpdate.getPrice());
		return bookReferenceToUpdate.getPrice();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getQuantityByTitle(String toFind) throws NoSuchElementException{
		Book bookToFind = getBookByTitle(toFind);
		return bookToFind.getQuantity();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getQuantityByID(Integer toFind) throws NoSuchElementException{
		Book bookToFind = getBookByID(toFind);
		return bookToFind.getQuantity();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getPriceByTitle(String toFind) throws NoSuchElementException{
		Book bookToFind = getBookByTitle(toFind);
		return bookToFind.getPrice();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getPriceByID(Integer toFind) throws NoSuchElementException{
		Book bookToFind = getBookByID(toFind);
		return bookToFind.getPrice();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InventoryMemento saveState() {
		InventoryMemento currentState = new InventoryMemento();
		currentState.setState("TitleMap", this.titleToIDMap);
		currentState.setState("IDMap", this.idToBookMap);
		return currentState;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void restoreState(InventoryMemento stateToRestore) {
		this.titleToIDMap = (Map<String, Integer>)stateToRestore.getState("TitleMap");
		this.idToBookMap = (Map<Integer, Book>)stateToRestore.getState("IDMap");
	}
	
	/**
	 * Get the reference of a book from the title.
	 * @param toFind	title of the book to find.
	 * @return	book referenced by the title.
	 * @throws NoSuchElementException if the book title doesn't exist in our inventory.
	 */
	private Book getBookByTitle(String toFind) throws NoSuchElementException{
		if (!this.existsBookTitle(toFind))
			throw new NoSuchElementException();
		Integer bookUniqueID = this.titleToIDMap.get(toFind);
		Book bookReferenceToFind = this.idToBookMap.get(bookUniqueID);
		return bookReferenceToFind;
	}
	
	/**
	 * Get the reference of a book from the unique ID.
	 * @param toFind	unique ID of the book to find.
	 * @return	book referenced by the unique ID.
	 * @throws NoSuchElementException if the unique ID doesn't exist in our inventory.
	 */
	private Book getBookByID(Integer toFind) throws NoSuchElementException {
		if (!this.existsBookID(toFind))
			throw new NoSuchElementException();
		Book bookReferenceToFind = this.idToBookMap.get(toFind);
		return bookReferenceToFind;
	}
	
	/**
	 * Check if a book reference exists within our database. Checks the unique ID.
	 * @param toCheck	Book with the unique ID to look for.
	 * @return	true if the unique ID exists in our database. False otherwise.
	 */
	private boolean existsBook(Book toCheck) {
		return this.existsBookID(toCheck.getID());
	}
	
	/**
	 * Check if a unique ID exists within our database.
	 * @param toCheck	unique ID to look for.
	 * @return	true if the unique ID exists in our database. False otherwise.
	 */
	private boolean existsBookID(Integer id) {
		if (this.idToBookMap.containsKey(id))
			return true;
		else
			return false;
	}
	
	/**
	 * Check if a title exists within our database.
	 * @param toCheck	title to look for.
	 * @return	true if the title exists in our database. False otherwise.
	 */
	private boolean existsBookTitle(String title) {
		if (this.titleToIDMap.containsKey(title))
			return true;
		else
			return false;
	}
}