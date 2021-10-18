package main;

import java.io.Serializable;

public interface Inventory extends Serializable{
	public double addBook(Book toAdd);
	public double sellBook(Book toSell);
	public double updatePrice(Book toUpdate);
	public int getQuantityByTitle(String toFind);
	public int getQuantityByID(Integer toFind);
	public double getPriceByTitle(String toFind);
	public double getPriceByID(Integer toFind);
}
