import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
   // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        assertTrue(palindrome.isPalindrome("noon"));
        assertTrue(palindrome.isPalindrome("ABA"));
        assertTrue(palindrome.isPalindrome("FOOF"));
        assertFalse(palindrome.isPalindrome("Noon"));
        assertTrue(palindrome.isPalindrome("racecar"));
        assertTrue(palindrome.isPalindrome("a"));
        assertTrue(palindrome.isPalindrome(""));
        assertTrue(palindrome.isPalindrome("*"));
        assertTrue(palindrome.isPalindrome("9"));
        assertFalse(palindrome.isPalindrome("palindrome"));
        assertFalse(palindrome.isPalindrome("you"));
        assertFalse(palindrome.isPalindrome("3ou"));
        assertTrue(palindrome.isPalindrome("323"));
        assertTrue(palindrome.isPalindrome("4334"));
        assertFalse(palindrome.isPalindrome("y3333"));
        assertFalse(palindrome.isPalindrome("123"));


    }

    @Test
    public void testIsPalindromeNew() {
        CharacterComparator offBy0 = new OffByN(0);
        CharacterComparator offByOne = new OffByOne();
        assertTrue(palindrome.isPalindrome("noon", offBy0));
        assertTrue(palindrome.isPalindrome("ABA", offBy0));
        assertTrue(palindrome.isPalindrome("FOOF", offBy0));
        assertFalse(palindrome.isPalindrome("Noon", offBy0));
        assertFalse(palindrome.isPalindrome("boa", offBy0));
        assertTrue(palindrome.isPalindrome("racecar", offBy0));
        assertTrue(palindrome.isPalindrome("a", offBy0));
        assertTrue(palindrome.isPalindrome("", offBy0));
        assertTrue(palindrome.isPalindrome("*", offBy0));
        assertTrue(palindrome.isPalindrome("9", offBy0));
        assertFalse(palindrome.isPalindrome("palindrome", offBy0));
        assertFalse(palindrome.isPalindrome("you", offBy0));
        assertFalse(palindrome.isPalindrome("3ou", offBy0));
        assertTrue(palindrome.isPalindrome("323", offBy0));
        assertTrue(palindrome.isPalindrome("4334", offBy0));
        assertFalse(palindrome.isPalindrome("y3333", offBy0));
        assertFalse(palindrome.isPalindrome("123", offBy0));

        assertTrue(palindrome.isPalindrome("flake", offByOne));
        assertTrue(palindrome.isPalindrome("tutu", offByOne));
        assertTrue(palindrome.isPalindrome("UNPOT", offByOne));
        assertTrue(palindrome.isPalindrome(" ", offByOne));
        assertTrue(palindrome.isPalindrome("d", offByOne));
        assertTrue(palindrome.isPalindrome("A", offByOne));
        assertTrue(palindrome.isPalindrome("!", offByOne));
        assertFalse(palindrome.isPalindrome("Flake", offByOne));
        assertTrue(palindrome.isPalindrome("FlakE", offByOne));
        assertTrue(palindrome.isPalindrome("135642", offByOne));
        assertTrue(palindrome.isPalindrome("13542", offByOne));
        assertTrue(palindrome.isPalindrome("`bdfeca", offByOne));
        assertFalse(palindrome.isPalindrome("az", offByOne));
        assertFalse(palindrome.isPalindrome("Asz", offByOne));
        assertFalse(palindrome.isPalindrome("chrysId", offByOne));

    }
}

