package kronaegit.wordle.possible;

public record CountRange(int from, int to) {
    public CountRange min(int min) {
        return new CountRange(Math.max(min, from), to);
    }
    public CountRange max(int max) {
        return new CountRange(from, Math.min(max, to));
    }

    public boolean isInRange(int i) {
        return from <= i && i <= to;
    }
}
