// SkipListSet.java

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedSet;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private static final double PROBABILITY = 0.5;
    private static final int MAX_LEVEL = 32; // Maximum number of levels

    private final Random random;

    // Main components of the SkipListSet
    private SkipListSetItem head; // Head (sentinel) node
    private int level;            // Current highest level
    private int size;             // Number of elements

    // Default constructor
    public SkipListSet() {
        this.random = new Random();
        this.level = 1;
        this.size = 0;

        // Create head node with maximum possible level
        this.head = new SkipListSetItem(null, MAX_LEVEL);

        // Initialize all forward pointers to null
        for (int i = 0; i < MAX_LEVEL; i++) {
            head.forward.add(null);
        }
    }

    // Constructor that takes a collection
    public SkipListSet(Collection<? extends T> c) {
        this();
        addAll(c);
    }

    // Randomly determine the level for a new node
    private int randomLevel() {
        int lvl = 1;
        while (lvl < MAX_LEVEL && random.nextDouble() < PROBABILITY) {
            lvl++;
        }
        return lvl;
    }

    // Add a value to the skip list
    @Override
    public boolean add(T value) {
        if (value == null) {
            throw new NullPointerException("Null values not allowed");
        }

        SkipListSetItem current = head;
        List<SkipListSetItem> update = new ArrayList<>(MAX_LEVEL);

        for (int i = 0; i < MAX_LEVEL; i++) {
            update.add(null);
        }

        for (int i = level - 1; i >= 0; i--) {
            while (current.forward.get(i) != null &&
                   current.forward.get(i).value.compareTo(value) < 0) {
                current = current.forward.get(i);
            }
            update.set(i, current);
        }

        current = current.forward.get(0);

        if (current != null && current.value.compareTo(value) == 0) {
            return false; // Duplicate
        }

        int nodeLevel = randomLevel();

        if (nodeLevel > level) {
            for (int i = level; i < nodeLevel; i++) {
                update.set(i, head);
            }
            level = nodeLevel;
        }

        SkipListSetItem newItem = new SkipListSetItem(value, nodeLevel);

        for (int i = 0; i < nodeLevel; i++) {
            newItem.forward.set(i, update.get(i).forward.get(i));
            update.get(i).forward.set(i, newItem);
        }

        if (newItem.forward.get(0) != null) {
            newItem.forward.get(0).backward = newItem;
        }

        newItem.backward = (update.get(0) == head) ? null : update.get(0);

        size++;
        return true;
    }

    // Check if value exists
    @Override
    public boolean contains(Object o) {
        if (o == null) return false;

        T value;
        try {
            value = (T) o;
        } catch (ClassCastException e) {
            return false;
        }

        SkipListSetItem current = head;

        for (int i = level - 1; i >= 0; i--) {
            while (current.forward.get(i) != null &&
                   current.forward.get(i).value.compareTo(value) < 0) {
                current = current.forward.get(i);
            }
        }

        current = current.forward.get(0);
        return current != null && current.value.compareTo(value) == 0;
    }

    // Remove a value
    @Override
    public boolean remove(Object o) {
        if (o == null) return false;

        T value;
        try {
            value = (T) o;
        } catch (ClassCastException e) {
            return false;
        }

        SkipListSetItem current = head;
        List<SkipListSetItem> update = new ArrayList<>(MAX_LEVEL);

        for (int i = 0; i < MAX_LEVEL; i++) {
            update.add(null);
        }

        for (int i = level - 1; i >= 0; i--) {
            while (current.forward.get(i) != null &&
                   current.forward.get(i).value.compareTo(value) < 0) {
                current = current.forward.get(i);
            }
            update.set(i, current);
        }

        current = current.forward.get(0);

        if (current == null || current.value.compareTo(value) != 0) {
            return false;
        }

        for (int i = 0; i < level; i++) {
            if (update.get(i).forward.get(i) != current) break;
            update.get(i).forward.set(i, current.forward.get(i));
        }

        if (current.forward.get(0) != null) {
            current.forward.get(0).backward = current.backward;
        }

        while (level > 1 && head.forward.get(level - 1) == null) {
            level--;
        }

        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new SkipListSetIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        for (T elem : this) {
            array[i++] = elem;
        }
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        if (a.length < size) {
            a = (E[]) java.lang.reflect.Array
                    .newInstance(a.getClass().getComponentType(), size);
        }

        int i = 0;
        for (T elem : this) {
            a[i++] = (E) elem;
        }

        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T elem : c) {
            if (add(elem)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object elem : c) {
            if (!contains(elem)) return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object elem : c) {
            if (remove(elem)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<T> it = iterator();

        while (it.hasNext()) {
            T elem = it.next();
            if (!c.contains(elem)) {
                remove(elem);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        head = new SkipListSetItem(null, MAX_LEVEL);
        for (int i = 0; i < MAX_LEVEL; i++) {
            head.forward.add(null);
        }
        level = 1;
        size = 0;
    }

    @Override
    public T first() {
        if (isEmpty()) throw new NoSuchElementException();
        return head.forward.get(0).value;
    }

    @Override
    public T last() {
        if (isEmpty()) throw new NoSuchElementException();

        SkipListSetItem current = head;
        for (int i = level - 1; i >= 0; i--) {
            while (current.forward.get(i) != null) {
                current = current.forward.get(i);
            }
        }
        return current.value;
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    public void reBalance() {
        List<T> elements = new ArrayList<>();
        for (T elem : this) {
            elements.add(elem);
        }

        clear();

        for (T elem : elements) {
            add(elem);
        }
    }

    // Node class
    private class SkipListSetItem {
        T value;
        List<SkipListSetItem> forward;
        SkipListSetItem backward;

        SkipListSetItem(T value, int level) {
            this.value = value;
            this.forward = new ArrayList<>(level);
            for (int i = 0; i < level; i++) {
                forward.add(null);
            }
        }
    }

    // Iterator class
    private class SkipListSetIterator implements Iterator<T> {
        private SkipListSetItem current = head.forward.get(0);

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            T value = current.value;
            current = current.forward.get(0);
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }
}
