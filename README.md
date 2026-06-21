# SkipListSet&lt;T&gt;

A generic, probabilistic skip list implemented in Java as a fully compliant `SortedSet<T>`.

Built from scratch as part of a Data Structures (CS II) assignment — no `ConcurrentSkipListSet`, no shortcuts.

---

## What is a Skip List?

A skip list is a probabilistic data structure that keeps elements in sorted order using multiple layers of linked lists. Each layer skips over more elements than the one below it, letting search, insert, and delete "skip" unnecessary comparisons — hence the name.

Expected time complexity across all three operations is **O(log n)**, comparable to a balanced BST, but achieved through randomization rather than rotation.

```
Level 3:  head ──────────────────────────────────► 42 ──────────────► null
Level 2:  head ──────────────► 17 ──────────────► 42 ──► 99 ────────► null
Level 1:  head ──► 5 ──► 12 ──► 17 ──► 28 ──► 42 ──► 63 ──► 99 ────► null
```

---

## Features

- **Fully generic** — `T extends Comparable<T>`, works with any naturally ordered type
- **Complete `SortedSet<T>` implementation** — drop-in replacement wherever a sorted set is needed
- **Expected O(log n)** insert, search, and delete
- **Backward pointer at level 0** for efficient reverse traversal
- **`reBalance()`** method to re-randomize node heights
- **`for-each` iteration** in sorted order via `Iterator<T>`
- Zero external dependencies — standard Java only

---

## Performance

Benchmarked against `TreeSet` and `LinkedList` across integers, doubles, and strings at scale:

| Dataset | Operation | SkipListSet | TreeSet |
|---|---|---|---|
| 100K integers | add | 127ms | 24ms |
| 100K integers | find (10K) | 7ms | 2ms |
| 1M integers | add | 1,752ms | 383ms |
| 1M integers | find (10K) | 17ms | 5ms |
| 10M integers | add | ~38s | ~13s |
| 10M integers | find (10K) | 37ms | 14ms |
| 100K strings (len 1K) | add | 114ms | 38ms |
| 1M doubles | find (10K) | 18ms | 6ms |

`LinkedList` is included for contrast — at 100K integers, find takes **1,034ms** vs **13ms** for `SkipListSet`.

> TreeSet consistently outperforms due to JVM-optimized red-black tree internals, but SkipListSet achieves the same asymptotic complexity with a simpler, more readable implementation.

---

## Usage

```java
SkipListSet<Integer> set = new SkipListSet<>();

set.add(10);
set.add(3);
set.add(7);

for (int x : set) {
    System.out.println(x);  // 3, 7, 10
}

set.contains(7);   // true
set.remove(3);     // true
set.first();       // 7
set.last();        // 10
set.size();        // 2
```

Construct from an existing collection:

```java
List<String> words = List.of("banana", "apple", "cherry");
SkipListSet<String> sorted = new SkipListSet<>(words);
// iterates: apple, banana, cherry
```

Works with any `Comparable` type — `Integer`, `Double`, `String`, or your own class that implements `Comparable<T>` (see `InterfacesExample.java` for a worked example with a custom class).

---

## API Reference

### Core `SortedSet` Methods

| Method | Description |
|---|---|
| `add(T value)` | Insert a value; returns `false` if already present |
| `remove(Object o)` | Remove a value; returns `false` if not found |
| `contains(Object o)` | O(log n) membership test |
| `first()` / `last()` | Min/max element |
| `size()` / `isEmpty()` | Collection metadata |
| `clear()` | Reset the structure |
| `iterator()` | In-order traversal |
| `toArray()` | Snapshot as array |
| `addAll` / `removeAll` / `retainAll` / `containsAll` | Bulk operations |
| `comparator()` | Returns `null` (natural ordering) |

### Extension Method

| Method | Description |
|---|---|
| `reBalance()` | Re-randomize all node heights — collect, clear, reinsert |

### Not Implemented

`headSet`, `subSet`, and `tailSet` throw `UnsupportedOperationException`.

---

## Design

### Node Structure (`SkipListSetItem`)

Each node stores:
- A `value` of type `T`
- A `List<SkipListSetItem> forward` — one pointer per level
- A `SkipListSetItem backward` — single backward pointer at level 0

### Head Node

A sentinel `head` node spans the full `MAX_LEVEL` height. It holds no value and serves as the entry point for all traversals.

### Level Generation

Node heights are drawn from a geometric distribution:

```java
private int randomLevel() {
    int lvl = 1;
    while (lvl < MAX_LEVEL && random.nextDouble() < PROBABILITY) {
        lvl++;
    }
    return lvl;
}
```

`PROBABILITY = 0.5`, `MAX_LEVEL = 32`.

---

## Requirements

- Java 8 or newer
- No external libraries

---

## Repository Contents

```
.
├── SkipListSet.java          # Skip list implementation
├── InterfacesExample.java    # Example: custom Comparable class with TreeSet
└── README.md
```

---

## Background

Developed as a Data Structures (CS II) project with three goals:

1. Understand how probabilistic balancing produces O(log n) behavior
2. Implement a complete Java collection interface from scratch
3. Build something that holds up at scale — the implementation handles **10 million elements**