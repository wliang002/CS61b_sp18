public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int front;
    private int back;
    private int _size = 8;
    private int RFACTOR = 2;

    public ArrayDeque() {
        items = (T[]) new Object[_size];
        size = 0;
        front = 0;
        back = size;

    }

    private void resize(int cap) {
        T [] arry = (T[]) new Object[cap];
        if (front > back) {
            System.arraycopy(items, front, arry, 0, size - front);
            System.arraycopy(items, 0, arry, size - front, front);
        } else {
            System.arraycopy(items, 0, arry, 0, size);
        }
        front = 0;
        back = size - 1;
        items = arry;
    }

    private void downSize() {
        if (items.length < _size * 2 || size >= items.length / 4) {
            return;
        } else {
            T [] arry = (T[]) new Object[items.length / RFACTOR];
            if (front > back) {
                if (items.length - front < size) {
                    System.arraycopy(items, front, arry, 0, items.length - front);
                    System.arraycopy(items, 0, arry,
                            items.length - front, size - (items.length - front));
                } else {
                    System.arraycopy(items, front, arry, 0, items.length - front);
                }
            } else {
                System.arraycopy(items, front, arry, 0, size);
            }
            front = 0;
            back = size - 1;
            items = arry;
        }

    }

    public void addFirst(T item) {
        if (size == 0) {
            items[front] = item;
            size += 1;
        } else {
            if (size == items.length) {
                resize(size * RFACTOR);
            }
            if (front == 0) {
                front = items.length - 1;
                items[front] = item;
            } else {
                front -= 1;
                items[front] = item;
            }
            size += 1;
        }
    }

    public void addLast(T item) {
        if (size == 0) {
            items[back] = item;
            size += 1;
        } else {
            if (size == items.length) {
                resize(size * RFACTOR);
            }
            if (back == items.length - 1) {
                back = 0;
                items[back] = item;
            } else {
                back += 1;
                items[back] = item;
            }
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
        if (size == 0) {
            return;
        }
        if (front < back) {
            for (int a = front; a <= back; a++) {
                System.out.print(items[a] + " ");
            }
        } else {
            for (int b = front; b <= items.length - 1; b++) {
                System.out.print(items[b] + " ");
            }
            for (int c = 0; c <= back; c++) {
                System.out.print(items[c] + " ");
            }
        }
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            T item = items[front];
            items[front] = null;
            if (front == items.length - 1) {
                front = 0;
            } else {
                front += 1;
            }
            size -= 1;
            if (size == 0) {
                front = back = 0;
            }
            downSize();
            return item;
        }
    }


    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            T item = items[back];
            items[back] = null;
            if (size == 1) {
                back = 0;
            } else {
                if (back == 0) {
                    back = items.length - 1;
                } else {
                    back -= 1;
                }
            }
            size -= 1;
            if (size == 0) {
                front = back = 0;
            }
            downSize();
            return item;
        }
    }


    public T get(int index) {
        if (size == 0 || index >= size) {
            return null;
        }
        if (index == 0) {
            return items[front];
        }

        if (front > back) {
            if (index < items.length - front) {
                return items[index + front];
            } else {
                return items[index - items.length + front];
            }
        }
        return items[index + front];

    }


}
