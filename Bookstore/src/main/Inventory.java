package main;

import java.io.IOException;
import java.io.Serializable;
import java.util.NoSuchElementException;

public interface Inventory extends Serializable{
	public int addBook(Book toAdd) throws IOException;
	public int sellBook(Book toSell) throws IOException, NoSuchElementException;
	public double updatePrice(Book toUpdate) throws IOException, NoSuchElementException;
	public int getQuantityByTitle(String toFind);
	public int getQuantityByID(Integer toFind);
	public double getPriceByTitle(String toFind);
	public double getPriceByID(Integer toFind);
}
