# Bookstore

An inventory used to manage books. Supports memento pattern for periodically saving a snapshot of the database and command pattern for logging a list of commands since the last snapshot.

In this way, in the event of a system crash, the snapshot can be restored and commands replayed to recover the state of the database exactly as it was at the time of the crash.

# Question 5
Instead  of  creating  decorator(s)  you  might  be  tempted  to  make  the  Inventory  class  create the  commands,  execute  the  commands  and  save  them.  Why  is  the  decorator  a  better  idea.

By having the decorator do this, we are separating the responsibilities. Each class should have one abstraction and the inventory object is already an abstraction. By putting it all in the inventory class, the class becomes bloated and difficult to understand. Also, by having it in the decorator, we can choose to run the inventory without the serialization of commands and memento functionality.

All these things make the code more modular and easy to add additional functionality or strip away functionality that isn't needed.
