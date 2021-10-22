package main;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Inventory of books.
 * @author Jordan
 *
 */
public interface Inventory{

	/**
	 * Add a book to our database. If the unique ID exists, increment the existing 
	 * count by the 'quanity' stored in the book object. Adds a new copy of the book
	 * and not the reference passed in.
	 * @param toSell	Book object containing the unique ID of the book in our 
	 * 					database and the quantity of copies to add. Title and 
	 * 					price are ignored if the unique ID exists in our database.
	 * @return the number of copies of total copies with this book's unique ID after the addition.
	 */
	public int addBook(Book toAdd) throws IOException;
	
	/**
	 * Sells copies of the book from our database.
	 * @param toSell	Book object containing the unique ID of the book in our 
	 * 					database and the quantity of copies to sell. Title and 
	 * 					price are ignored.
	 * @return the number of copies of this book remaining after the sale.
	 * @throws NoSuchElementException 	if the book's unique ID does not exist in the 
	 * 									database or there are insufficient copies to sell.
	 */
	public int sellBook(Book toSell) throws IOException, NoSuchElementException;
	
	/**
	 * Updates the price of a book within our database.
	 * @param toUpdate	Book object containing the unique ID of the book in our 
	 * 					database and the value of the new price. Title and 
	 * 					quantity are ignored.
	 * @return the new price of the book in our database.
	 * @throws NoSuchElementException if the book's unique ID does not exist in the database.
	 */
	public double updatePrice(Book toUpdate) throws IOException, NoSuchElementException;
	
	/**
	 * Find the price of the book in our database.
	 * @param toFind	title of the book to find.
	 * @returnthe number of copies of the requested book remaining.
	 * @throws NoSuchElementException if the title does not exist in the database.
	 */
	public int getQuantityByTitle(String toFind) throws NoSuchElementException;
	
	/**
	 * Find the quantity of the book in our database.
	 * @param toFind	unique ID of the book to find.
	 * @returnthe number of copies of the requested book remaining.
	 * @throws NoSuchElementException if the ID does not exist in the database.
	 */
	public int getQuantityByID(Integer toFind) throws NoSuchElementException;
	
	/**
	 * Find the price of the book in our database.
	 * @param toFind	title of the book to find.
	 * @return the price of the requested book.
	 * @throws NoSuchElementException if the title does not exist in the database.
	 */
	public double getPriceByTitle(String toFind) throws NoSuchElementException;
	
	/**
	 * Find the quantity of the book in our database.
	 * @param toFind	unique ID of the book to find.
	 * @returnthe price of the requested book.
	 * @throws NoSuchElementException if the ID does not exist in the database.
	 */
	public double getPriceByID(Integer toFind) throws NoSuchElementException;
}
