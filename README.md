# Bookstore

An inventory used to manage books. Supports memento pattern for periodically saving a snapshot of the database (default is 10 commands) and command pattern for logging a list of commands since the last snapshot.

In this way, in the event of a system crash, the snapshot can be restored and commands replayed to recover the state of the database exactly as it was at the time of the crash.

# Purpose

The goal of this project was to understand the appropriate use of the two aforementioned design patterns by creating a persistent database for books. This project provided a real-world use case of advanced object oriented design patterns.

# Requirements

Developed on:
- JRE17
- JUnit5
