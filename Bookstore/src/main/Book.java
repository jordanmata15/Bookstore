package main;

public class Book {
	private String title;
	private int id;
	private int quantity;
	private double price;
	
	public Book(String title, int id, int quantity, Double price) {
		this.setTitle(title);
		this.setId(id);
		this.quantity = quantity;
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

	public void setId(int id) {
		this.id = id;
	}

	public double getPrice() {
		return this.price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}
	
}
