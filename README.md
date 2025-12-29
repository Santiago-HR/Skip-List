SkipListSet (Skip List Implementation in Java)

This repository contains a generic Java implementation of a Skip List, exposed as SkipListSet<T> that implements Java’s SortedSet<T> interface.

A skip list is a probabilistic, self-balancing data structure that maintains elements in sorted order and provides expected O(log n) time complexity for insertion, search, and deletion—comparable to balanced binary search trees, but often simpler to implement.

Features

Fully generic (T extends Comparable<T>)

Implements the complete SortedSet<T> interface

Expected O(log n) time complexity for:

Insert

Search

Delete

Randomized node heights (probability = 0.5)

Multi-level forward pointers

Backward pointer at level 0

Optional reBalance() method for re-randomizing node heights

Supports Java enhanced-for (for-each) iteration

Repository Contents
.
└── SkipListSet.java   # Generic skip list implementation

Requirements
Java 8 or newer


No external libraries are required.

Design Overview

Each element is stored in an internal node (SkipListSetItem) containing:

A payload value

A list of forward pointers (one per level)

A backward pointer at level 0

A sentinel head node spans the maximum height of the skip list.
Node heights are generated randomly using a geometric distribution.

Configuration Parameters

Maximum level: 32

Probability factor: 0.5

Ordering: Natural ordering (Comparable<T>)

Supported Operations
SortedSet Methods

add, addAll

remove, removeAll, retainAll

contains, containsAll

first, last

size, isEmpty, clear

toArray (both versions)

iterator

comparator (returns null for natural ordering)

Unsupported Methods

The following methods are intentionally not implemented and throw UnsupportedOperationException:

headSet

subSet

tailSet

Iterator Behavior

The internal iterator:

Traverses elements in sorted order

Returns element values (not internal nodes)

Supports hasNext() and next()

Does not support remove()

Example Usage
for (Integer x : skipList) {
    System.out.println(x);
}

Rebalancing
Method
public void reBalance()

Behavior
1. Collect all elements
2. Clear the skip list
3. Reinsert elements with newly randomized heights


This method is not invoked automatically and is intended for optional performance tuning.

Testing Notes

No main() method is included

Intended to be tested using:

A separate driver program, or

An external testing framework

Designed to handle large workloads (millions of elements)

Notes & Restrictions

Does not use Java’s built-in ConcurrentSkipListSet

Not thread-safe

Uses only standard Java collections internally

Performance varies based on JVM and hardware

Background

This implementation was developed as part of a Data Structures (CS II) assignment with the goals of:

Understanding probabilistic balancing

Implementing Java collection interfaces

Building scalable data structures from scratch
