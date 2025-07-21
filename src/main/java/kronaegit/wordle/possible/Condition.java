package kronaegit.wordle.possible;

import kronaegit.wordle.obj.Alphabet;
import kronaegit.wordle.obj.Word;
import kronaegit.wordle.obj.WordleColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Condition {
    public final PossibleAlphabets[] letters;
    public final Map<Alphabet, CountRange> counts = new HashMap<>();

    public Condition(int length) {
        this.letters = new PossibleAlphabets[length];
        for (int i = 0; i < length(); i++) {
            this.letters[i] = new PossibleAlphabets();
        }
        for (Alphabet alphabet : new PossibleAlphabets().getAlphabets()) {
            counts.put(alphabet, new CountRange(0, length));
        }
    }

    public int length() {
        return letters.length;
    }

    public void apply(Word word, WordleColor[] colors) {
        if(word.length != length() || length() != colors.length) throw new IllegalArgumentException();
        LetterInfo[] result = new LetterInfo[length()];
        for (int i = 0; i < length(); i++)
            result[i] = new LetterInfo(i, word.get(i), colors[i]);
        apply(result);
    }
    public void apply(LetterInfo @NotNull [] word) {
        for (LetterInfo info : word) {
            int at = info.index();
            Alphabet alphabet = info.alphabet();
            WordleColor color = info.color();

            if(color == WordleColor.GREEN) {
                letters[at].only(alphabet);
            } else {
                letters[at].remove(alphabet);
            }
            if(color == WordleColor.BLACK) {
                int sameNotBlackCount = 0;
                for (LetterInfo info2 : word) {
                    if(info2.alphabet() == alphabet && info2.color() != WordleColor.BLACK) {
                        sameNotBlackCount++;
                    }
                }
                counts.put(alphabet, new CountRange(sameNotBlackCount, sameNotBlackCount));
                if(sameNotBlackCount == 0) {
                    for (PossibleAlphabets letter : letters) {
                        letter.remove(alphabet);
                    }
                }
            } else if(color == WordleColor.YELLOW) {
                int sameYellows = 0;
                for (LetterInfo info2 : word) {
                    if(info2.alphabet() == alphabet && info2.color() == WordleColor.YELLOW) {
                        sameYellows++;
                    }
                }
                counts.put(alphabet, counts.get(alphabet).min(sameYellows));
            }
        }
    }

    public List<Word> list() {
        List<Word> results = new ArrayList<>();
        backtrack(results, new Word(length()), 0);
        return results;
    }
    private void backtrack(List<Word> results, Word current, int index) {
        if (index == length()) {
            Word word = current.clone();
            if(!isPossible(word)) return;
            results.add(word);
            return;
        }

        for (Alphabet alpha : letters[index].getAlphabets()) {
            current.set(index, alpha);
            backtrack(results, current, index + 1);
        }
    }

    public boolean isPossible(@NotNull Word word) {
        if(word.length != length()) return false;
        for (int i = 0; i < word.length; i++) {
            if(!letters[i].contains(word.get(i))) return false;
        }

        Map<Alphabet, Integer> freq = new HashMap<>();
        for (Alphabet a : word) {
            freq.put(a, freq.getOrDefault(a, 0) + 1);
        }

        for (Map.Entry<Alphabet, Integer> entry : freq.entrySet()) {
            Alphabet alphabet = entry.getKey();
            int count = entry.getValue();
            CountRange range = counts.get(alphabet);
            if (count < range.from() || count > range.to()) {
                return false;
            }
        }

        for (Map.Entry<Alphabet, CountRange> entry : counts.entrySet()) {
            Alphabet alphabet = entry.getKey();
            int count = freq.getOrDefault(alphabet, 0);
            CountRange range = entry.getValue();
            if (count < range.from() || count > range.to()) {
                return false;
            }
        }

        return true;
    }
}
