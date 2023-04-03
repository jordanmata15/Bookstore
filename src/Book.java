package main;

import java.io.Serializable;

/**
 * Book object to be stored in inventory object.
 * @author Jordan
 *
 */
public class Book implements Serializable{
	
	/** Base version */
	private static final long serialVersionUID = 1L;
	
	private String title;
	private int id;
	private int quantity;
	private double price;

	/**
	 * Book constructor
	 * @param title		the title of the book.
	 * @param id		the unique ID of this book.
	 * @param quantity 	number of books to store initially.
	 * @param price		the price of the book.
	 * @throws IllegalArgumentException If ID, price, or quantity are negative.
	 */
	public Book(String title, int id, int quantity, Double price) throws IllegalArgumentException{
		this.title = title;
		this.setID(id);
		this.setQuantity(quantity);
		this.setPrice(price);
	}

	/**
	 * @return Title of the book.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @return ID of this book (greater than or equal to 0). 
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * @param id	Identifier of this book.
	 * @throws IllegalArgumentException		If negative id passed in.
	 */
	public void setID(int id) throws IllegalArgumentException{
		if (id < 0)
			throw new IllegalArgumentException("ID must be greater than or equal to zero.");
		this.id = id;
	}

	/**
	 * @return price of the book (greater than or equal to 0).
	 */
	public double getPrice() {
		return this.price;
	}

	/**
	 * @param price 	price of the book (greater than or equal to 0).
	 * @throws IllegalArgumentException		If negative price passed in.
	 */
	public void setPrice(double price) throws IllegalArgumentException{
		if (price < 0)
			throw new IllegalArgumentException("Price must be greater than or equal to zero.");
		this.price = price;
	}

	/**
	 * @return quantity of the book (greater than or equal to 0).
	 */
	public int getQuantity() {
		return this.quantity;
	}
	
	/**
	 * @param quantity 	quantity of the book (greater than or equal to 0).
	 * @throws IllegalArgumentException		If negative quantity passed in.
	 */
	private void setQuantity(int quant) throws IllegalArgumentException{
		if (quant < 0)
			throw new IllegalArgumentException("Quantity must be greater than or equal to zero.");
		this.quantity = quant;
	}

	/**
	 * Add or subtract quantity of this book.
	 * @param quantity	positive to add to existing quantity, negative to 
	 * 					subtract from quantity.
	 * @throws IllegalArgumentException if the quantity to add/subtract brings the stored
	 * 									quantity below 0.
	 */
	public void incrementQuantity(int quantity) throws IllegalArgumentException {
		if (this.quantity+quantity < 0)
			throw new IllegalArgumentException();
		this.quantity += quantity;
	}
	
	/**
	 * @return string representation of the book.
	 */
	@Override
	public String toString() {
		return "<" + 
					this.title + "_" + 
					this.id + "_" + 
					this.quantity + "_" + 
					this.price + 
				">";
	}
}
