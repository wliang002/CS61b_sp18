public class Palindrome {

    public Deque<Character> wordToDeque(String word) {
        Deque<Character> dw = new ArrayDeque<>();
        for (int i = 0; i < word.length(); i++) {
            dw.addLast(word.charAt(i));
        }
        return dw;
    }

    public boolean isPalindrome(String word) {
        Deque<Character> nw = wordToDeque(word);
        if (nw.size() <= 1) {
            return true;
        } else {
            while (nw.size() > 1) {
                if (nw.removeFirst() != nw.removeLast()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        Deque<Character> nw = wordToDeque(word);
        if (nw.size() <= 1) {
            return true;
        } else {
            while (nw.size() > 1) {
                if (!cc.equalChars(nw.removeFirst(), nw.removeLast())) {
                    return false;
                }
            }
        }
        return true;
    }

}
