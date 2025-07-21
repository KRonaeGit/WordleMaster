package kronaegit.wordle.possible.rater;

import kronaegit.wordle.obj.Alphabet;
import kronaegit.wordle.obj.Word;
import kronaegit.wordle.possible.Condition;
import kronaegit.wordle.possible.PossibleAlphabets;

import java.util.ArrayList;
import java.util.List;

public class RaterV1 implements Rater {
    public int rate(Condition conditions, Word word) {
        List<Alphabet> toEliminate = new ArrayList<>();
        for (PossibleAlphabets letter : conditions.letters) {
            for (Alphabet alphabet : letter.getAlphabets()) {
                if(toEliminate.contains(alphabet)) continue;
                toEliminate.add(alphabet);
            }
        }
        List<Alphabet> eliminated = new ArrayList<>();
        for (int i = 0; i < word.length; i++) {
            Alphabet alphabet = word.get(i);
            if(toEliminate.contains(alphabet) && !eliminated.contains(alphabet))
                eliminated.add(alphabet);
        }
        return word.length - eliminated.size();
    }

    @Override
    public String getName() {
        return "RaterV1";
    }
}
