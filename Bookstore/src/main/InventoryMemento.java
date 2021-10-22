package main;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Generic memento used to store fields of the Inventory object.
 * @author Jordan
 *
 */
class InventoryMemento implements Serializable{
	
	/** Base version */
	private static final long serialVersionUID = 1L;
	
	private Hashtable<String, Object> savedState = new Hashtable<String, Object>();
	
	/**
	 * InventoryMemento Constructor. Accessible by this package only.
	 */
	protected InventoryMemento(){}
	
	/**
	 * Store a field of the internal state of the inventory.
	 * @param stateName		descriptor of what is being stored (for later restoration).
	 * @param stateValue	field to store.
	 */
	protected void setState(String stateName, Object stateValue) { 
		savedState.put(stateName, stateValue);
	}
	
	/**
	 * Restore a field of the internal state of the inventory.
	 * @param stateName	descriptor of what is being restored.
	 * @return	field to restore.
	 */
	protected Object getState(String stateName) {
		return this.savedState.get(stateName);
	}
}
