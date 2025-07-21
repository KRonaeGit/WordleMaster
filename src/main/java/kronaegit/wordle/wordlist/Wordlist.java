package kronaegit.wordle.wordlist;

import kronaegit.wordle.obj.Word;

import java.util.ArrayList;

public class Wordlist extends ArrayList<Word> {
    public Wordlist() {}
    public Wordlist(Wordlist wordlist) {
        super(wordlist);
    }
}
