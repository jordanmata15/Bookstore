package main;

import java.io.Serializable;

public class Book implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String title;
	private int id;
	private int quantity;
	private double price;
	
	public Book(String title, int id, int quantity, Double price) {
		this.setTitle(title);
		this.setID(id);
		this.setQuantity(quantity);
		this.setPrice(price);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getID() {
		return this.id;
	}

	public void setID(int id) throws IllegalArgumentException{
		if (id < 0)
			throw new IllegalArgumentException("ID must be greater than or equal to zero.");
		this.id = id;
	}

	public double getPrice() {
		return this.price;
	}

	public void setPrice(double price) throws IllegalArgumentException{
		if (price < 0)
			throw new IllegalArgumentException("Price must be greater than or equal to zero.");
		this.price = price;
	}

	public int getQuantity() {
		return this.quantity;
	}
	
	private void setQuantity(int quant) throws IllegalArgumentException{
		if (quant < 0)
			throw new IllegalArgumentException("Quantity must be greater than or equal to zero.");
		this.quantity = quant;
	}

	public void incrementQuantity(int quantity) {
		this.quantity += quantity;
	}
	
	@Override
	public String toString() {
		return this.title + "_" + this.id;
	}
}
