package kronaegit.wordle.program;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WordleArgs {
    public int wordLength = 5;
    public int maxChance = 6;
    public File wordlistFile = null;
    public File wordlistDir = null;

    public static @NotNull WordleArgs parse(String[] args) throws IllegalArgumentException, IOException {
        WordleArgs config = new WordleArgs();
        Iterator<String> it = Arrays.asList(args).iterator();

        while (it.hasNext()) {
            String arg = it.next();
            switch (arg) {
                case "-h":
                case "--help":
                    printHelp();
                    System.exit(0);
                    break;
                case "--word-length":
                case "-l": {
                    if (!it.hasNext()) throw new IllegalArgumentException("Missing value for --word-length");
                    int val = Integer.parseInt(it.next());
                    if (val <= 0) throw new IllegalArgumentException("Word length must be greater than 0");
                    config.wordLength = val;
                    break;
                }
                case "--chance":
                case "-c": {
                    if (!it.hasNext()) throw new IllegalArgumentException("Missing value for --chance");
                    int val = Integer.parseInt(it.next());
                    if (val <= 0) throw new IllegalArgumentException("Chance must be greater than 0");
                    config.maxChance = val;
                    break;
                }
                case "--word-list":
                case "-wdl": {
                    if (!it.hasNext()) throw new IllegalArgumentException("Missing value for --word-list");
                    if (config.wordlistDir != null)
                        throw new IllegalArgumentException("--word-list and --word-lists cannot be used together");
                    File file = new File(it.next());
                    if (!file.exists()) throw new IOException("Word list file does not exist: " + file);
                    if (!file.isFile()) throw new IOException("Word list is not a file: " + file);
                    config.wordlistFile = file;
                    break;
                }
                case "--word-lists":
                case "-wdls": {
                    if (!it.hasNext()) throw new IllegalArgumentException("Missing value for --word-lists");
                    if (config.wordlistFile != null)
                        throw new IllegalArgumentException("--word-list and --word-lists cannot be used together");
                    File dir = new File(it.next());
                    if (!dir.exists()) dir.mkdirs();
                    if (!dir.isDirectory()) throw new IOException("Word lists path is not a directory: " + dir);
                    config.wordlistDir = dir;
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        if(config.wordlistFile == null && config.wordlistDir == null) {
            config.wordlistDir = new File("wordlist");
        }
        return config;
    }

    private static void printHelp() {
        System.out.println("Usage: java -jar WordleMaster-" + WordleMaster.version + ".jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h,  --help               Show this help message and exit.");
        System.out.println("  -l, --word-length <int> Set word length (must be > 0). Default: 5");
        System.out.println("  -c,  --chance      <int> Set max number of guesses (must be > 0). Default: 6");
        System.out.println("  -wdl,--word-list   <file>  Use a single wordlist file.");
        System.out.println("  -wdls,--word-lists <dir>   Use a directory of wordlists to choose from.");
        System.out.println();
    }

}
