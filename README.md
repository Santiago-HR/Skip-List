<div align="center">

<h1>SkipListSet&lt;T&gt;</h1>

<p>A probabilistic, generic sorted set — built from scratch in Java.</p>

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Data_Structures-005C84?style=for-the-badge&logo=stackexchange&logoColor=white"/>
<img src="https://img.shields.io/badge/Algorithms-O(log_n)-2ea44f?style=for-the-badge"/>
<img src="https://img.shields.io/badge/UCF-COP3503-black?style=for-the-badge&logo=academia&logoColor=white"/>

<br/><br/>

```
╔══════════════════════════════════════════════════════════════════╗
║  Level 4 │ head ───────────────────────────────► [42] ─────► ∅  ║
║  Level 3 │ head ──────────────► [17] ──────────► [42] ──► ∅     ║
║  Level 2 │ head ──► [5] ──────► [17] ──► [28] ──► [42] ──► ∅   ║
║  Level 1 │ head ──► [5] ──► [12] ──► [17] ──► [28] ──► [42] ──► ∅ ║
╚══════════════════════════════════════════════════════════════════╝
              Each level an express lane — O(log n) guaranteed
```

</div>

---

## Why a Skip List?

Most sorted collections use trees that enforce balance through rotations. A skip list does something different — it **randomizes the structure** instead of enforcing it. Each inserted node gets a random height. Taller nodes act as express lanes. The result: the same O(log n) performance as a balanced BST, with far simpler insertion and deletion logic.

This project implements that algorithm as a complete Java `SortedSet<T>`, meaning it's a drop-in replacement for `TreeSet` anywhere natural ordering applies.

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     SkipListSet<T>                      │
│              implements SortedSet<T>                    │
│                                                         │
│  ┌──────────────────────┐   ┌────────────────────────┐ │
│  │   SkipListSetItem    │   │  SkipListSetIterator   │ │
│  │  ┌────────────────┐  │   │                        │ │
│  │  │ T value        │  │   │  Walks level 0 L → R   │ │
│  │  │ forward[]      │──┼──►│  Returns T values,     │ │
│  │  │ backward       │  │   │  not item wrappers     │ │
│  │  └────────────────┘  │   └────────────────────────┘ │
│  └──────────────────────┘                               │
└─────────────────────────────────────────────────────────┘
```

**`SkipListSetItem`** — Node wrapper. Holds a payload, an `ArrayList` of forward pointers (one per level), and a single backward pointer at level 0.

**`SkipListSetIterator`** — Walks level 0 in sorted order, returning payload values. Powers Java's `for-each` loop.

**`SkipListSet`** — The public face. Manages a sentinel head node, current max level, and routes all operations through multi-level traversal.

---

## Node Height Distribution

Heights are drawn from a geometric distribution — simple, elegant, and probabilistically sound:

```java
private int randomLevel() {
    int lvl = 1;
    while (lvl < MAX_LEVEL && random.nextDouble() < 0.5)
        lvl++;
    return lvl;
}
```

| Height | Probability |
|--------|------------|
| 1 | 50% |
| 2 | 25% |
| 3 | 12.5% |
| 4 | 6.25% |
| … | … |

`MAX_LEVEL = 32` · `PROBABILITY = 0.5`

---

## Benchmark

Tested against `TreeSet` (JVM red-black tree) and `LinkedList` (O(n) baseline):

| n | Structure | Add | Find 10K | Delete 5K |
|---|-----------|-----|----------|-----------|
| 100K | LinkedList | 4ms | **1,034ms** | 471ms |
| 100K | **SkipListSet** | 142ms | **13ms** | 8ms |
| 100K | TreeSet | 41ms | 5ms | 3ms |
| 1M | **SkipListSet** | 1,752ms | 17ms | 30ms |
| 1M | TreeSet | 383ms | 5ms | 3ms |
| 10M | **SkipListSet** | ~38s | 37ms | 20ms |
| 10M | TreeSet | ~13s | 14ms | 5ms |

> SkipListSet beats `LinkedList` by **~80×** on search at 100K elements. TreeSet wins overall due to JVM C-level internals, but SkipListSet matches its asymptotic class entirely from scratch.

---

## Usage

```java
// Any Comparable type works
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

// Construct from existing collection
SkipListSet<String> words = new SkipListSet<>(List.of("delta", "alpha", "gamma"));
// iterates: alpha, delta, gamma

// Re-randomize all node heights (optional tuning)
set.reBalance();
```

---

## API

| Category | Methods |
|----------|---------|
| **Mutation** | `add`, `addAll`, `remove`, `removeAll`, `retainAll`, `clear` |
| **Query** | `contains`, `containsAll`, `first`, `last`, `size`, `isEmpty` |
| **Export** | `iterator`, `toArray` (both forms) |
| **SortedSet** | `comparator` → `null` (natural ordering) |
| **Unsupported** | `headSet`, `subSet`, `tailSet` → `UnsupportedOperationException` |
| **Extension** | `reBalance()` — re-randomizes all node heights |

---

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Generics](https://img.shields.io/badge/Generics-informational?style=flat-square)
![SortedSet](https://img.shields.io/badge/SortedSet%3CT%3E-interface-blue?style=flat-square)
![Probabilistic](https://img.shields.io/badge/Probabilistic-Data_Structure-9cf?style=flat-square)
![Scale](https://img.shields.io/badge/Tested_at-10M_elements-success?style=flat-square)

*UCF COP 3503 Computer Science II — Summer 2023*

</div>