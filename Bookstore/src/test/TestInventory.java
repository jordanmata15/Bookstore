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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.Book;
import main.CommandDecorator;
import main.ConcreteInventory;
import main.Inventory;
import main.PersistentInventory;

class TestInventory {

	String logPath = "C:\\Users\\Jordan\\git\\Bookstore\\Bookstore";
	Inventory inventory;
	List<Book> bookList;
	
	@BeforeEach
	void setUp() throws Exception {
		this.clearLogFiles(this.logPath);
		
		inventory = new ConcreteInventory();
		inventory = new PersistentInventory(this.logPath);
		inventory = new CommandDecorator(inventory, this.logPath);
		
		bookList = new ArrayList<Book>(19);
	
		bookList.add(new Book("A Tale of Two Cities", 				11, 0, 13.99));
		bookList.add(new Book("Communist Manifesto", 				3, 0, 2.23));
		bookList.add(new Book("Harry Potter", 						16, 0, 11.01));
		bookList.add(new Book("The Hunger Games", 					7, 0, 3.99));
		bookList.add(new Book("To Kill a MockingBird", 				0, 0, 0.99));
		bookList.add(new Book("The Great Gatsby", 					2, 0, 20.19));
		bookList.add(new Book("Java: A Headfirst Approach", 		15, 0, 122.99));
		bookList.add(new Book("C By Design", 						4, 0, 8.99));
		bookList.add(new Book("Lord of the Rings", 					9, 0, 4.01));
		bookList.add(new Book("The Girl with the Dragon Tattoo", 	8, 0, 21.78));
		bookList.add(new Book("The Hobbit", 						1, 0, 13.99));
		bookList.add(new Book("Cat in the Hat", 					13, 0, 2.23));
		bookList.add(new Book("The Giver", 							6, 0, 11.01));
		bookList.add(new Book("1984", 								17, 0, 3.99));
		bookList.add(new Book("Storm Front", 						10, 0, 0.99));
		bookList.add(new Book("Angels and Demons", 					12, 0, 20.19));
		bookList.add(new Book("Don Quixote", 						5, 0, 122.99));
		bookList.add(new Book("The Catcher in the Rye", 			14, 0, 8.99));
		bookList.add(new Book("Charlotte's Web", 					18, 0, 4.01));
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	//@Test
	void testDoNothing() {}

	@Test
	void testAddBook() {
		List<Book> originalList = new ArrayList<Book>();
		this.bookList.forEach(book->originalList.add(new Book(book.getTitle(), 
															book.getID(), 
															book.getQuantity(), 
															book.getPrice())));
		Collections.shuffle(bookList);
		
		for (Book book : this.bookList) {
			assertEquals(this.inventory.addBook(book), 0.0);
		}	
		
		
		this.bookList.forEach(book->this.testBookExistence(this.inventory, book));
		
		
		Inventory toRecoverToConcrete = new ConcreteInventory();
		this.bookList.forEach(book->this.testNonExistenceException(toRecoverToConcrete, book));
		
		Inventory toRecoverSnapshot = new PersistentInventory(this.logPath);
		this.bookList.subList(0,10).forEach(book->this.testBookExistence(toRecoverSnapshot, book));
		this.bookList.subList(10,19).forEach(book->this.testNonExistenceException(toRecoverSnapshot, book));
		
		Inventory toRecoverToWithReplay = new CommandDecorator(toRecoverToConcrete, this.logPath);
		this.bookList.subList(0,10).forEach(book->this.testNonExistenceException(toRecoverToWithReplay, book));
		this.bookList.subList(10, 19).forEach(book->this.testBookExistence(toRecoverToWithReplay, book));
		
		
		Inventory toRecoverSnapAndReplay = new CommandDecorator(toRecoverSnapshot, this.logPath);
		this.bookList.forEach(book->this.testBookExistence(toRecoverSnapAndReplay, book));
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

	private void clearLogFiles(String folder) {
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
			e.printStackTrace();
		}
	}
}
