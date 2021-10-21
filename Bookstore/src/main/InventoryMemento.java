package main;

import java.io.Serializable;
import java.util.Hashtable;

class InventoryMemento implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Hashtable<String, Object> savedState = new Hashtable<String, Object>();
	
	protected InventoryMemento(){}
	
	protected void setState(String stateName, Object stateValue) { 
		savedState.put(stateName, stateValue);
	}
	
	protected Object getState(String stateName) {
		return this.savedState.get(stateName);
	}
}
