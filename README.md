<div align="center">

<h1>SkipListSet&lt;T&gt;</h1>
<p>A probabilistic sorted set — implemented from scratch in Java, with zero shortcuts.</p>

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Data_Structures-005C84?style=for-the-badge&logo=stackexchange&logoColor=white"/>
<img src="https://img.shields.io/badge/Complexity-O(log_n)-2ea44f?style=for-the-badge"/>
<img src="https://img.shields.io/badge/UCF-COP3503-black?style=for-the-badge&logo=academia&logoColor=white"/>
<img src="https://img.shields.io/badge/Tests-14%2F14_passing-brightgreen?style=for-the-badge"/>

<br/><br/>

```
╔═══════════════════════════════════════════════════════════════════╗
║  Level 4 │ head  ──────────────────────────────► [42] ──────► ∅  ║
║  Level 3 │ head  ──────────────► [17] ──────────► [42] ───► ∅    ║
║  Level 2 │ head  ──► [5] ───────► [17] ──► [28] ──► [42] ──► ∅  ║
║  Level 1 │ head  ──► [5] ──► [12] ──► [17] ──► [28] ──► [42] ──► ∅ ║
╚═══════════════════════════════════════════════════════════════════╝
              Each level is an express lane — O(log n) on average
```

</div>

---

## The Idea

Most sorted collections — `TreeSet`, `PriorityQueue` — enforce balance through rotations. A skip list takes a different bet: **randomize the structure, don't enforce it**. Each node gets a random height on insertion. Taller nodes act as express lanes that let traversal skip over large sections of the list. On average, this produces O(log n) search, insert, and delete without a single rotation.

This project implements that algorithm as a complete Java `SortedSet<T>` — a drop-in replacement for `TreeSet` for any type with natural ordering.

---

## Architecture

Three classes work together inside a single `.java` file:

```
┌──────────────────────────────────────────────────────────────┐
│               SkipListSet<T>                                 │
│        extends AbstractSet<T>                                │
│        implements SortedSet<T>                               │
│                                                              │
│  ┌─────────────────────────┐   ┌──────────────────────────┐ │
│  │     SkipListSetItem     │   │   SkipListSetIterator    │ │
│  │  ┌─────────────────┐   │   │                          │ │
│  │  │  T value        │   │   │  Walks level-0 L → R     │ │
│  │  │  List forward[] │───┼──►│  Returns T payloads,     │ │
│  │  │  Item backward  │   │   │  never internal nodes    │ │
│  │  └─────────────────┘   │   └──────────────────────────┘ │
│  └─────────────────────────┘                                │
└──────────────────────────────────────────────────────────────┘
```

**`SkipListSetItem`** — The node. Stores a payload value, an `ArrayList` of forward pointers (one per level), and a single backward pointer at level 0.

**`SkipListSetIterator`** — Walks level 0 left-to-right in sorted order, returning payload values. Powers Java's enhanced `for` loop.

**`SkipListSet`** — The public interface. Manages a sentinel head node spanning all levels, tracks the current max level, and routes every operation through the multi-level traversal logic.

---

## Node Height Distribution

```java
private int randomLevel() {
    int lvl = 1;
    while (lvl < MAX_LEVEL && random.nextDouble() < 0.5)
        lvl++;
    return lvl;
}
```

| Height | Probability | Role |
|--------|-------------|------|
| 1 | 50% | Base level only |
| 2 | 25% | One express lane |
| 3 | 12.5% | Two express lanes |
| 4 | 6.25% | Three express lanes |
| … | … | … |

`MAX_LEVEL = 32` · `PROBABILITY = 0.5`

The result is a geometric distribution — most nodes are short, a few are tall, and tall nodes naturally create the fast paths that make O(log n) possible without any explicit balancing.

---

## Benchmark

Tested against `TreeSet` (JVM red-black tree) and `LinkedList` (O(n) baseline) across integers, doubles, and strings:

| n | Structure | Add | Find 10K | Delete 5K |
|---|-----------|-----|----------|-----------|
| 100K | LinkedList | 4ms | **1,034ms** | 471ms |
| 100K | **SkipListSet** | 142ms | **13ms** | 8ms |
| 100K | TreeSet | 41ms | 5ms | 3ms |
| 1M | **SkipListSet** | 1,752ms | 17ms | 30ms |
| 1M | TreeSet | 383ms | 5ms | 3ms |
| 10M | **SkipListSet** | ~38s | 37ms | 20ms |
| 10M | TreeSet | ~13s | 14ms | 5ms |

> At 100K elements, `SkipListSet` is **~80× faster than `LinkedList`** on search. `TreeSet` wins on raw speed due to JVM C-level internals, but `SkipListSet` achieves the same asymptotic complexity, built entirely from scratch.

---

## Quick Start

```java
// Works with any Comparable type
SkipListSet<Integer> set = new SkipListSet<>();
set.add(15);
set.add(3);
set.add(9);

for (int n : set) System.out.print(n + " ");
// → 3 9 15

set.first();       // 3
set.last();        // 15
set.contains(9);   // true
set.remove(3);     // true
set.size();        // 2

// Construct from existing collection
SkipListSet<String> words = new SkipListSet<>(List.of("delta", "alpha", "gamma"));
// iterates: alpha, delta, gamma

// Re-randomize all node heights (optional performance tuning)
set.reBalance();
```

---

## Full API

| Category | Methods |
|----------|---------|
| **Mutation** | `add`, `addAll`, `remove`, `removeAll`, `retainAll`, `clear` |
| **Query** | `contains`, `containsAll`, `first`, `last`, `size`, `isEmpty` |
| **Export** | `iterator`, `toArray` (both forms) |
| **SortedSet** | `comparator` → `null` (natural ordering) |
| **Not Supported** | `headSet`, `subSet`, `tailSet` → `UnsupportedOperationException` |
| **Extension** | `reBalance()` — collect all elements, clear, reinsert with new random heights |

---

## Bug Fixed

| Issue | Root Cause | Fix |
|-------|-----------|-----|
| `equals()` always returned `false` between two sets with identical contents; `hashCode()` was non-deterministic | `SkipListSet` implemented `SortedSet<T>` directly, so it inherited `Object.equals()` (identity comparison) and `Object.hashCode()` (memory address) instead of the correct set-equality semantics | Changed declaration to `extends AbstractSet<T> implements SortedSet<T>` — `AbstractSet` provides `equals()` (same size + mutual containment) and `hashCode()` (sum of element hash codes), satisfying the full `Set` contract |

---

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![AbstractSet](https://img.shields.io/badge/extends-AbstractSet%3CT%3E-informational?style=flat-square)
![SortedSet](https://img.shields.io/badge/implements-SortedSet%3CT%3E-blue?style=flat-square)
![Generics](https://img.shields.io/badge/Generics-T_extends_Comparable-9cf?style=flat-square)
![Scale](https://img.shields.io/badge/Tested_at-10M_elements-success?style=flat-square)

*UCF COP 3503 Computer Science II — Summer 2023*

</div>