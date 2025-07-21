package kronaegit.wordle.wordlist;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class WordlistInputStream implements Closeable {
    private final DataInputStream in;
    private int currentLength = -1;

    public WordlistInputStream(File file) throws FileNotFoundException {
        this(new DataInputStream(new FileInputStream(file)));
    }
    public WordlistInputStream(DataInputStream in) {
        this.in = in;
    }

    public String nextWord() throws IOException {
        byte first;
        if((first = in.readByte()) == 0) return null;
        if(first == 1) {
            if ((currentLength = in.readUnsignedByte()) < 1)
                throw new IOException("Length never can be 0.");
            return nextWord();
        }
        if(currentLength < 1) throw new IOException("Length is not set.");

        byte[] wordBytes = new byte[currentLength];
        wordBytes[0] = first;
        if(currentLength > 1)
            if(currentLength-1 != in.read(wordBytes, 1, currentLength-1))
                throw new EOFException();
        return new String(wordBytes, StandardCharsets.US_ASCII);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
