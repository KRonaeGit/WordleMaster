package kronaegit.wordle.possible.rater;

import kronaegit.wordle.obj.Alphabet;
import kronaegit.wordle.obj.Word;
import kronaegit.wordle.possible.Condition;

public interface Rater {
    int rate(Condition conditions, Word word);
    String getName();
}
