package kronaegit.wordle.wordlist;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class WordlistOutputStream implements Closeable {
    private final DataOutputStream out;
    private int currentLength = -1;

    public WordlistOutputStream(File file) throws FileNotFoundException {
        this(new DataOutputStream(new FileOutputStream(file)));
    }
    public WordlistOutputStream(DataOutputStream out) {
        this.out = out;
    }

    public void writeWord(String word) throws IOException {
        writeWord(word.getBytes(StandardCharsets.US_ASCII));
    }
    public void writeWord(byte[] word) throws IOException {
        if(word.length == 0) return;
        if(word[0]==0) throw new IllegalArgumentException("First byte of word never can be NUL(0).");
        if(currentLength != word.length) {
            out.writeByte(1);
            out.writeByte(currentLength = word.length);
        }
        out.write(word);
    }

    @Override
    public void close() throws IOException {
        out.writeByte(0);
        out.close();
    }
}
