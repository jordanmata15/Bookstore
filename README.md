# Bookstore

An inventory used to manage books. Supports memento pattern for periodically saving a snapshot of the database and command pattern for logging a list of commands since the last snapshot.

In this way, in the event of a system crash, the snapshot can be restored and commands replayed to recover the state of the database exactly as it was at the time of the crash.

## Notes
// TODO
