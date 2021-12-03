package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.Book;
import main.SimpleInventory;
import main.Inventory;
import main.PersistentInventory;

class TestInventory {

	String logPath = "."; // full file path to where to log your serialized files to
	Inventory originalInventory ;
	List<Book> bookListZero;
	List<Book> bookListNonZero;
	
	@BeforeEach
	void setUp() throws Exception {
		this.clearLogFiles(this.logPath);
		
		originalInventory = new SimpleInventory();
		originalInventory = new PersistentInventory((SimpleInventory)originalInventory, this.logPath);
		
		bookListZero = new ArrayList<Book>(19);
		bookListNonZero = new ArrayList<Book>(19);
	
		// books with quantity = 0
		bookListNonZero.add(new Book("A Tale of Two Cities", 				11, 11, 1.99));
		bookListNonZero.add(new Book("Communist Manifesto", 				3, 10, 20.23));
		bookListNonZero.add(new Book("Harry Potter", 						16, 9, 1.01));
		bookListNonZero.add(new Book("The Hunger Games", 					7, 3, 30.99));
		bookListNonZero.add(new Book("To Kill a MockingBird", 				0, 190, 10.99));
		bookListNonZero.add(new Book("The Great Gatsby", 					2, 8, 2.19));
		bookListNonZero.add(new Book("Java: A Headfirst Approach", 			15, 2, 12.99));
		bookListNonZero.add(new Book("C By Design", 						4, 10, 0.99));
		bookListNonZero.add(new Book("Lord of the Rings", 					9, 90, 14.01));
		bookListNonZero.add(new Book("The Girl with the Dragon Tattoo", 	8, 3, 2.78));
		bookListNonZero.add(new Book("The Hobbit", 							1, 63, 1.99));
		bookListNonZero.add(new Book("Cat in the Hat", 						13, 3, 20.23));
		bookListNonZero.add(new Book("The Giver", 							6, 10, 1.01));
		bookListNonZero.add(new Book("1984", 								17, 3, 3.99));
		bookListNonZero.add(new Book("Storm Front", 						10, 8, 10.99));
		bookListNonZero.add(new Book("Angels and Demons", 					12, 20, 20.19));
		bookListNonZero.add(new Book("Don Quixote", 						5, 3, 12.99));
		bookListNonZero.add(new Book("The Catcher in the Rye", 				14, 52, 80.99));
		bookListNonZero.add(new Book("Charlotte's Web", 					18, 8, 4.01));
		
		// books with quantity = 0
		bookListZero.add(new Book("A Tale of Two Cities", 				11, 0, 13.99));
		bookListZero.add(new Book("Communist Manifesto", 				3, 0, 2.23));
		bookListZero.add(new Book("Harry Potter", 						16, 0, 11.01));
		bookListZero.add(new Book("The Hunger Games", 					7, 0, 3.99));
		bookListZero.add(new Book("To Kill a MockingBird", 				0, 0, 0.99));
		bookListZero.add(new Book("The Great Gatsby", 					2, 0, 20.19));
		bookListZero.add(new Book("Java: A Headfirst Approach", 		15, 0, 122.99));
		bookListZero.add(new Book("C By Design", 						4, 0, 8.99));
		bookListZero.add(new Book("Lord of the Rings", 					9, 0, 4.01));
		bookListZero.add(new Book("The Girl with the Dragon Tattoo", 	8, 0, 21.78));
		bookListZero.add(new Book("The Hobbit", 						1, 0, 13.99));
		bookListZero.add(new Book("Cat in the Hat", 					13, 0, 2.23));
		bookListZero.add(new Book("The Giver", 							6, 0, 11.01));
		bookListZero.add(new Book("1984", 								17, 0, 13.99));
		bookListZero.add(new Book("Storm Front", 						10, 0, 0.99));
		bookListZero.add(new Book("Angels and Demons", 					12, 0, 2.19));
		bookListZero.add(new Book("Don Quixote", 						5, 0, 122.99));
		bookListZero.add(new Book("The Catcher in the Rye", 			14, 0, 8.99));
		bookListZero.add(new Book("Charlotte's Web", 					18, 0, 14.01));
	}
 
	@AfterEach
	void tearDown() throws Exception {
		this.clearLogFiles(this.logPath);
	}
	
	@Test
	void testSellBook() throws NoSuchElementException, IOException {
		
		try {
			this.originalInventory.sellBook(new Book("Does Not Exist", 10888, 0, 10.0));
			fail("Didn't throw exception for selling a nonexistent book!");
		} catch (NoSuchElementException e) {
			// pass
		}
		
		for (Book book : this.bookListNonZero)
			assertEquals(this.originalInventory.addBook(book), book.getQuantity());
		
		for (Book book : this.bookListNonZero)
			assertEquals(this.originalInventory.sellBook(book), 0);
		
		int numToAdd = 20;
		int numToSell = 5;
		int oldCount = this.originalInventory.getQuantityByTitle("A Tale of Two Cities");
		assertEquals(oldCount, 0);
		this.originalInventory.addBook(new Book("A Tale of Two Cities", 11, numToAdd, 13.99));
		assertEquals(this.originalInventory.getQuantityByTitle("A Tale of Two Cities"), numToAdd);
		assertEquals(this.originalInventory.sellBook(new Book("A Tale of Two Cities", 11, numToSell, 13.99)), 
						numToAdd-numToSell);
		assertEquals(this.originalInventory.getQuantityByTitle("A Tale of Two Cities"), 
						numToAdd-numToSell);
	}
	
	@Test
	void testUpdatePrice() throws IOException {
		
		try {
			this.originalInventory.updatePrice(new Book("Does Not Exist", 10888, 0, 10.0));
			fail("Didn't throw exception for updating a nonexistent book!");
		} catch (NoSuchElementException e) {
			// pass
		}

		for (Book book : this.bookListNonZero)
			assertEquals(this.originalInventory.addBook(book), book.getQuantity());
		
		double oldPrice = this.originalInventory.getPriceByTitle("Storm Front");
		double newPrice = 10.50;
		
		assertEquals(this.originalInventory.getPriceByTitle("Storm Front"), oldPrice);
		assertEquals(this.originalInventory.updatePrice(new Book("Storm Front", 10, 0, newPrice)), 
						newPrice);
		assertEquals(this.originalInventory.getPriceByTitle("Storm Front"), 
						newPrice);
	}

	@Test
	void testAdd() throws Exception {
		
		for (Book book : this.bookListNonZero)
			assertEquals(this.originalInventory.addBook(book), book.getQuantity());
		
		this.bookListNonZero.forEach(book->this.testBookExistence(this.originalInventory , book));
		
		/* test for adding 2x the original amount of books */
		for (Book book : this.bookListNonZero)
			assertEquals(this.originalInventory.addBook(book), 2*book.getQuantity());
		
		this.bookListNonZero.forEach(book->{
			Book updatedCopies = new Book(book.getTitle(), 
											book.getID(), 
											2*book.getQuantity(), 
											book.getPrice());
			this.testBookExistence(this.originalInventory , updatedCopies);
		});
		
		/* test for adding 3x the original amount of books */
		for (Book book : this.bookListNonZero)
			assertEquals(this.originalInventory.addBook(book), 3*book.getQuantity());
		
		this.bookListNonZero.forEach(book->{
			Book updatedCopies = new Book(book.getTitle(), 
											book.getID(), 
											3*book.getQuantity(), 
											book.getPrice());
			this.testBookExistence(this.originalInventory , updatedCopies);
		});
		
		/* test to add 10 to a random book */
		int newCopiesN = 10;
		Book catcher = new Book("The Catcher in the Rye", 14, newCopiesN, 80.99);
		int originalCount = this.originalInventory.getQuantityByID(catcher.getID());
		
		this.originalInventory.addBook(catcher);
		int newCount = this.originalInventory.getQuantityByTitle(catcher.getTitle());
		assertEquals(newCount, originalCount+newCopiesN);
		
		/* add a random book */
		int numCopiesToAdd = 2;
		Book notExistsYet = new Book("Some Hot New Title!", 100, numCopiesToAdd, 10.22);
		
		try { // make sure it doesn't exist yet
			this.originalInventory.getQuantityByTitle(notExistsYet.getTitle());
			fail("Failed to throw exception for book that doesn't exist!");
		} catch (NoSuchElementException e) { /* pass */}
		
		this.originalInventory.addBook(notExistsYet);
		int numCopiesAdded = this.originalInventory.getQuantityByTitle(notExistsYet.getTitle());
		assertEquals(numCopiesToAdd, numCopiesAdded);
	}
	
	
	
	@Test
	void testRecoverInventory() throws Exception {
		List<Book> originalList = new ArrayList<Book>();
		this.bookListZero.forEach(book->originalList.add(new Book(book.getTitle(), 
																book.getID(), 
																book.getQuantity(), 
																book.getPrice())));
		Collections.shuffle(bookListZero);
		
		for (Book book : this.bookListZero)
			assertEquals(this.originalInventory.addBook(book), 0.0);
		
		this.bookListZero.forEach(book->this.testBookExistence(this.originalInventory , book));
		
		Inventory emptyInventory = new SimpleInventory();
		Inventory toRecoverSnapAndReplay = new PersistentInventory((SimpleInventory)emptyInventory, this.logPath);
		this.bookListZero.forEach(book->this.testBookExistence(toRecoverSnapAndReplay, book));
		
		// commands since last snapshot on toRecoverSnapAndReplay = 9
		
		
		// set a new quantity for items 5-15
		Collections.shuffle(bookListNonZero);
		bookListNonZero.subList(5, 15).forEach(book->{
			try {
				toRecoverSnapAndReplay.addBook(book);
			} catch (Exception e1) {
				e1.printStackTrace();
				fail("Cannot add a valid book!!");
			}
		});
		bookListNonZero.subList(5, 15).forEach(book->{	
			double copies = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(copies, book.getQuantity());
			assertEquals(copies>0, true);
		});
		bookListNonZero.subList(0, 5).forEach(book->{	
			double price = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(price, 0);
		});
		bookListNonZero.subList(15, 19).forEach(book->{	
			double price = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(price, 0);
		});
		
		// commands since last snapshot on toRecoverSnapAndReplay = 9
		
		// sell a copy for items 7-12, both 0-5 and 16-19 should have 0 quantity
		bookListNonZero.subList(7, 12).forEach(book->{
			try {
				toRecoverSnapAndReplay.sellBook(new Book(book.getTitle(), 
															book.getID(), 
															2,
															book.getPrice()));
			} catch (Exception e1) {
				e1.printStackTrace();
				fail("Cannot sell a valid book!");
			}
		});
		bookListNonZero.subList(7, 12).forEach(book->{	
			int newQuant = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(newQuant, book.getQuantity()-2);
		});
		bookListNonZero.subList(0, 5).forEach(book->{	
			int newQuant = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(newQuant, 0);
		});
		bookListNonZero.subList(5, 7).forEach(book->{	
			int newQuant = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(newQuant, book.getQuantity());
		});
		bookListNonZero.subList(12, 15).forEach(book->{	
			int newQuant = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(newQuant, book.getQuantity());
		});
		bookListNonZero.subList(15, 19).forEach(book->{	
			int newQuant = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			assertEquals(newQuant, 0);
		});
		
		// commands since last snapshot on toRecoverSnapAndReplay = 4
		
		// try and fail to sell a copy for items 15-19 (none exist)
		bookListNonZero.subList(15, 19).forEach(book->{					
			try{
				toRecoverSnapAndReplay.sellBook(new Book(book.getTitle(), 
															book.getID(), 
															1,
															book.getPrice()));
				fail("Didn't catch exception for selling books when none exists! Title: " + book.getTitle());
			}
			catch(Exception e) {/* pass */}
		});
		
		// try and fail to sell a copy for items 15-19 (none exist)
		bookListNonZero.subList(15, 19).forEach(book->{					
			try{
				toRecoverSnapAndReplay.updatePrice(new Book(book.getTitle(), 
															book.getID()+19, 
															book.getQuantity(),
															1.99));
				fail("Didn't catch exception for updating book price when none exists! Title: " + book.getTitle());
			}
			catch(Exception e) {/* pass */}
		});
		
		// commands since last snapshot on toRecoverSnapAndReplay = 4
		
		// set a new price for items 0-4
		bookListNonZero.subList(0, 5).forEach(book->{
			try {
				toRecoverSnapAndReplay.updatePrice(book);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Cannot update price of valid book!");
			}
		});
		bookListNonZero.subList(0, 5).forEach(book->{	
			double origPrice = this.originalInventory.getPriceByID(book.getID());
			double newPrice = toRecoverSnapAndReplay.getPriceByID(book.getID());
			assertEquals(newPrice, book.getPrice());
			assertEquals(newPrice!=origPrice, true);
		});
		bookListNonZero.subList(5, 19).forEach(book->{	
			double origPrice = this.originalInventory.getPriceByID(book.getID());
			double price = toRecoverSnapAndReplay.getPriceByID(book.getID());
			assertEquals(price, origPrice);
		});
		
		// try to recover the mix of actions we performed using the memento and log
		Inventory newInventory = new SimpleInventory();
		newInventory = new PersistentInventory((SimpleInventory)newInventory, this.logPath);
		
		for (Book book:bookListNonZero) {
			double priceFromOldInventory = toRecoverSnapAndReplay.getPriceByID(book.getID());
			double priceFromNewInventory = newInventory.getPriceByID(book.getID());
			int quantFromOldInventory = toRecoverSnapAndReplay.getQuantityByID(book.getID());
			int quantFromNewInventory = newInventory.getQuantityByID(book.getID());
			assertEquals(priceFromOldInventory, priceFromNewInventory);
			assertEquals(quantFromOldInventory, quantFromNewInventory);
		}
	}
	
	
	public void testNonExistenceException(Inventory inventoryToTest, Book book){
		try {
			assertEquals(inventoryToTest.getPriceByID(book.getID()), book.getPrice()); 
			fail("Didn't catch exception for non existent book");
		} catch (NoSuchElementException e){ /*pass*/ }
		
		try {
			assertEquals(inventoryToTest.getPriceByTitle(book.getTitle()), book.getPrice());
			fail("Didn't catch exception for non existent book");
		} catch (NoSuchElementException e){ /*pass*/ }
		
		try {
			assertEquals(inventoryToTest.getQuantityByID(book.getID()), book.getQuantity());
			fail("Didn't catch exception for non existent book");
		} catch (NoSuchElementException e){ /*pass*/ }
		
		try {
			assertEquals(inventoryToTest.getQuantityByTitle(book.getTitle()), book.getQuantity());
			fail("Didn't catch exception for non existent book");
		} catch (NoSuchElementException e){ /*pass*/ }
	}
	
	public void testBookExistence(Inventory inventoryToTest, Book book){
		assertEquals(inventoryToTest.getPriceByID(book.getID()), book.getPrice());
		assertEquals(inventoryToTest.getPriceByTitle(book.getTitle()), book.getPrice());
		assertEquals(inventoryToTest.getQuantityByID(book.getID()), book.getQuantity());
		assertEquals(inventoryToTest.getQuantityByTitle(book.getTitle()), book.getQuantity());
	}

	private void clearLogFiles(String folder) throws IOException {
		List<File> commandLogFiles = new ArrayList<File>();
		String commandLogRegex = ".+?.ser";
		try {
			commandLogFiles = Files.list(Paths.get(folder))
							        .filter(Files::isRegularFile)
							        .map(Path::toFile)
							        .filter(file->Pattern.matches(commandLogRegex, file.getName()))
							        .collect(Collectors.toList());
			commandLogFiles.forEach(file->file.delete());	
		} catch (IOException e) {
			throw e;
		}
	}
}
