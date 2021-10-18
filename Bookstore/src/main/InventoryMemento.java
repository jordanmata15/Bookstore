package main;

import java.io.Serializable;

public class InventoryMemento implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Inventory savedState;
	
	public InventoryMemento(Inventory toSave){
		this.savedState = toSave;
	}
	
	public Inventory getState() {
		return savedState;
	}
}
