package kronaegit.wordle.obj;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Word implements Iterable<Alphabet> {
    private final Alphabet[] alphabets;
    public final int length;

    public Word(@NotNull String str) {
        this(str.length());
        for (int i = 0; i < alphabets.length; i++)
            alphabets[i] = Alphabet.fromChar(str.charAt(i));
    }

    public Word(int length) {
        this.length = length;
        this.alphabets = new Alphabet[length];
    }

    public Alphabet get(int index) {
        return alphabets[index];
    }
    public Alphabet set(int index, Alphabet alphabet) {
        return alphabets[index] = alphabet;
    }

    @Override
    public Word clone() {
        Word copy = new Word(this.length);
        if (this.length >= 0)
            System.arraycopy(this.alphabets, 0, copy.alphabets, 0, this.length);
        return copy;
    }

    @Override
    public Iterator<Alphabet> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < length;
            }

            @Override
            public Alphabet next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return alphabets[index++];
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder word = new StringBuilder();
        for (Alphabet alphabet : alphabets)
            word.append(alphabet.toString());
        return word.toString();
    }
}
