package kronaegit.wordle.possible.rater;

import kronaegit.wordle.obj.Alphabet;
import kronaegit.wordle.obj.Word;
import kronaegit.wordle.possible.Condition;
import kronaegit.wordle.possible.CountRange;

import java.util.*;

public class RaterV2 implements Rater {

    @Override
    public int rate(Condition conditions, Word word) {
        Map<Alphabet, Integer> alphabetCounts = new HashMap<>();
        for (Alphabet alphabet : word) {
            alphabetCounts.put(alphabet, alphabetCounts.getOrDefault(alphabet, 0) + 1);
        }

        int score = 0;
        Set<Alphabet> alreadyDeductedDuplicates = new HashSet<>();

        for (int i = 0; i < word.length; i++) {
            Alphabet ch = word.get(i);

            // Case A: Fixed correct letter reused at fixed position — not helpful
            if (conditions.letters[i].getAlphabets().size() == 1
                    && conditions.letters[i].contains(ch)) {
                score--;
                continue;
            }

            // Case B: Duplicates — now consider if count limit is undefined
            CountRange range = conditions.counts.get(ch);
            int usedCount = alphabetCounts.getOrDefault(ch, 0);
            boolean hasLimit = 1 < range.from() || range.to() < word.length;

            if (!alreadyDeductedDuplicates.contains(ch)) {
                if (hasLimit) {
                    int excess = usedCount - range.to();
                    if (excess > 0) {
                        score -= excess;
                    }
                } else {
                    if (usedCount > 1) {
                        score -= usedCount - 1;
                    }
                }
                alreadyDeductedDuplicates.add(ch);
            }

            // Case C: Not allowed in this position
            if (!conditions.letters[i].contains(ch)) {
                score--;
            }
        }

        return score;
    }

    @Override
    public String getName() {
        return "RaterV2";
    }
}
