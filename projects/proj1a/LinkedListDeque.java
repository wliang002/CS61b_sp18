public class LinkedListDeque<T> {
    private class DLNode {
        T item;
        DLNode prev, next;

        DLNode(T i, DLNode p, DLNode n) {
            this.item = i;
            this.prev = p;
            this.next = n;
        }
    }

    private DLNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new DLNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        if (size == 0) {
            DLNode node = new DLNode(item, sentinel, sentinel);
            sentinel.next = node;
            sentinel.prev = node;
            size += 1;
        } else {
            DLNode node = new DLNode(item, sentinel, sentinel.next);
            sentinel.next.prev = node;
            sentinel.next = node;
            size += 1;
        }
    }

    public void addLast(T item) {
        if (size == 0) {
            DLNode node = new DLNode(item, sentinel, sentinel);
            sentinel.next = node;
            sentinel.prev = node;
            size += 1;
        } else {
            DLNode node = new DLNode(item, sentinel.prev, sentinel);
            sentinel.prev.next = node;
            sentinel.prev = node;
            size += 1;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        DLNode p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        DLNode fn = sentinel.next;
        sentinel.next = fn.next;
        fn.next.prev = sentinel;
        size -= 1;
        return fn.item;
    }


    public T removeLast() {
        if (size == 0) {
            return null;
        }
        DLNode ln = sentinel.prev;
        sentinel.prev = ln.prev;
        ln.prev.next = sentinel;
        size -= 1;
        return ln.item;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        DLNode p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecur(index, sentinel.next);
    }

    private T getRecur(int index, DLNode n) {
        if (index == 0) {
            return n.item;
        }
        return getRecur(index - 1, n.next);
    }


}
