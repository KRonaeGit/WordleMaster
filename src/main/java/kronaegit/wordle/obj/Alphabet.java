package kronaegit.wordle.obj;

import org.jetbrains.annotations.NotNull;

public enum Alphabet {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    public static Alphabet fromChar(char c) {
        c = Character.toUpperCase(c);
        if (c < 'A' || c > 'Z')
            throw new IllegalArgumentException("Character is not a valid alphabet letter: " + c);
        return Alphabet.valueOf(String.valueOf(c));
    }
}
