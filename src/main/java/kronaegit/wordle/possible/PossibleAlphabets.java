package kronaegit.wordle.possible;

import kronaegit.wordle.obj.Alphabet;

import java.util.ArrayList;
import java.util.List;
import static kronaegit.wordle.obj.Alphabet.*;

public class PossibleAlphabets {
    private static final List<Alphabet> everyAlphabets = List.of(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z);

    public final List<Alphabet> alphabets;
    public PossibleAlphabets() {
        this.alphabets = new ArrayList<>(everyAlphabets);
    }

    public List<Alphabet> getAlphabets() {
        return alphabets;
    }
    public void remove(Alphabet alphabet) {
        alphabets.remove(alphabet);
    }

    public void only(Alphabet alphabet) {
        alphabets.clear();
        alphabets.add(alphabet);
    }

    public boolean contains(Alphabet alphabet) {
        return alphabets.contains(alphabet);
    }
}
