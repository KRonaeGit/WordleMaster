package kronaegit.wordle.obj;

import org.jetbrains.annotations.NotNull;

public enum WordleColor {
    BLACK, YELLOW, GREEN;

    public static WordleColor @NotNull [] fromString(@NotNull String str) {
        WordleColor[] colors = new WordleColor[str.length()];
        for (int i = 0; i < colors.length; i++)
            colors[i] = WordleColor.fromChar(str.charAt(i));
        return colors;
    }
    public static WordleColor fromChar(char c) {
        c = Character.toUpperCase(c);
        if(c == 'B') return WordleColor.BLACK;
        if(c == 'Y') return WordleColor.YELLOW;
        if(c == 'G') return WordleColor.GREEN;
        throw new IllegalArgumentException();
    }
}
