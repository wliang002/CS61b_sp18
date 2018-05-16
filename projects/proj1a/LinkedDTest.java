import org.junit.Test;
import static org.junit.Assert.*;
public class LinkedDTest {
    @Test
    public void testPowers() {
        LinkedListDeque l = new LinkedListDeque();
        assertTrue(l.isEmpty());
        l.addFirst(0);
        l.addFirst(1);
        assertFalse(l.isEmpty());
        assertEquals(0, l.removeLast());
    }

    @Test
    public void testGet() {
        LinkedListDeque l = new LinkedListDeque();
        l.addFirst(0);
        l.addFirst(1);
        l.addLast(2);
        assertEquals(2, l.removeLast());
        assertEquals(0, l.get(1));
        assertEquals(null, l.get(3));
        assertEquals(2, l.size());
        l.addFirst(8);
        l.addLast(9);
        assertEquals(8, l.removeFirst());
        assertEquals(9, l.get(2));
        l.printDeque();
    }

    @Test
    public void testArrayaddFirst() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(0);
        a.addFirst(1);
        a.addLast(2);
        a.addFirst(3);
        System.out.println(a.get(2));

    }

    @Test
    public void testArrayaddlast() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addLast(22);
        a.addFirst(9);
        a.addFirst(7);
        a.addFirst(6);
        a.addFirst(5);
        a.addLast(23);
        a.addFirst(4);
        a.addFirst(2);
        a.addFirst(1);
        a.addFirst(0);
//        assertEquals((Object) 1, a.get(0));
//        assertEquals((Object) 1, a.removeFirst());
//        assertEquals((Object) 0, a.get(0));
//        assertEquals((Object) 0, a.get(0));
//        assertEquals((Object) 0, a.get(0));
//        a.addFirst(7);
//        a.addFirst(8);
//        a.addFirst(9);
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.removeLast();
//        assertEquals((Object) 0, a.removeLast());
//        a.addFirst(11);
//        a.addFirst(12);
//        a.addFirst(13);
//        assertEquals((Object) 7, a.removeLast());
//        a.addLast(15);
//        assertEquals((Object) 13, a.get(0));
//        a.addLast(17);
//        assertEquals((Object) 12, a.get(1));


    }

    @Test
    public void testArrayadd() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(0);
        a.addFirst(1);
        assertEquals((Object) 0, a.get(1));
        assertEquals((Object) 0, a.removeLast());
        a.addFirst(4);
        a.addFirst(5);
        assertEquals((Object) 1, a.removeLast());
        a.addLast(7);
        assertEquals((Object) 7, a.removeLast());
        a.addFirst(9);
        assertEquals((Object) 9, a.removeFirst());
        assertEquals((Object) 4, a.removeLast());
        assertEquals((Object) 5, a.get(0));
        assertEquals((Object) 5, a.removeFirst());
        a.addFirst(14);
        assertEquals((Object) 14, a.removeLast());
    }
}



