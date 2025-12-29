SkipListSet (Skip List Implementation in Java)

This repository contains a generic Java implementation of a Skip List, exposed as a SkipListSet<T> that implements Java’s SortedSet<T> interface.

A skip list is a probabilistic, self-balancing data structure that maintains elements in sorted order while providing expected O(log n) time complexity for insertion, search, and deletion—similar in performance to balanced binary search trees, but often simpler to implement.

📁 Repository Contents
.
└── SkipListSet.java   # Generic skip list implementation

✨ Features

Fully generic: supports any type T that implements Comparable<T>

Implements the full SortedSet<T> interface

Expected O(log n) performance for:

add

remove

contains

Uses randomized node heights (probability = 0.5)

Supports:

Forward pointers at multiple levels

Backward pointer at level 0

Includes a reBalance() method to re-randomize node heights

Compatible with Java enhanced-for (for-each) loops

🧠 Design Overview
Skip List Structure

Each element is wrapped in an internal node class (SkipListSetItem)

Nodes store:

A payload value

A list of forward pointers (one per level)

A backward pointer at level 0

A head sentinel node spans the maximum allowed height

Node heights are randomly generated using a geometric distribution

Key Parameters

Maximum level: 32

Probability: 0.5

Ordering: Natural ordering (Comparable<T>)

🔌 Interfaces Implemented

The class is designed to behave exactly like a standard Java sorted collection.

Implemented Interfaces

SortedSet<T>

Set<T>

Collection<T>

Iterable<T>

This means a SkipListSet can be used anywhere a SortedSet is expected.

🛠 Supported Operations
SortedSet Methods

add, addAll

remove, removeAll, retainAll

contains, containsAll

first, last

size, isEmpty, clear

toArray (both versions)

iterator

comparator (returns null for natural ordering)

Unsupported Operations

The following methods are intentionally not implemented and will throw UnsupportedOperationException:

headSet

subSet

tailSet

🔁 Iterator Support

The internal SkipListSetIterator:

Iterates over elements in sorted order

Returns payload values, not internal nodes

Supports hasNext() and next()

Does not support remove() (throws exception)

This allows seamless use with enhanced-for loops:

for (Integer x : skipList) {
    System.out.println(x);
}

🔄 Rebalancing
public void reBalance()


Collects all elements

Clears the skip list

Re-inserts elements with newly randomized heights

This method is not called automatically and is intended for optional performance tuning.

⚙️ Requirements

Java 8 or newer

No external dependencies

🧪 Testing Notes

This class does not include a main() method

Intended to be tested using:

A separate driver program, or

An external test harness

Designed to handle large-scale workloads (millions of elements)

🚫 Restrictions

Does not use Java’s built-in ConcurrentSkipListSet

Uses basic Java collections internally where appropriate

Not thread-safe

📚 Background

This implementation was originally developed as part of a Data Structures (CS II) programming assignment, with the goal of:

Understanding probabilistic balancing

Implementing Java collection interfaces

Designing efficient, scalable data structures from scratch
