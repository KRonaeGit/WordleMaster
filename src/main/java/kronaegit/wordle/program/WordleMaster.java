package kronaegit.wordle.program;

import kronaegit.wordle.obj.Word;
import kronaegit.wordle.obj.WordleColor;
import kronaegit.wordle.possible.Condition;
import kronaegit.wordle.possible.rater.Rater;
import kronaegit.wordle.possible.rater.RaterV2;
import kronaegit.wordle.wordlist.Wordlist;
import kronaegit.wordle.wordlist.WordlistInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class WordleMaster {
    public static int length = 5;
    public static int maxChance = 6;
    public static int chance = -1;

    private static Condition condition;
    private static Wordlist possibleAnswers;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Wordlist wordlist = loadWordlist(scanner, args);

        while (true) {
            System.out.println();

            // Init
            condition = new Condition(length);
            possibleAnswers = new Wordlist(wordlist);
            chance = maxChance;

            while (true) {
                // Calculate Recommends
                Word randomEliminator = recommendWord(wordlist);
                Word randomPossibleAnswer = pickRandom(possibleAnswers);
                int p = (int) (100f / possibleAnswers.size());

                // Print Recommends
                printRecommendation(randomEliminator, randomPossibleAnswer, p);

                // User Input
                Word word = inputWord(scanner);
                if (word == null) continue;

                WordleColor[] colors = inputColors(scanner);
                if (colors == null) continue;

                if (isCorrect(colors)) {
                    System.out.println("\nCongrates!\n");
                    break;
                }

                // Apply Conditions
                chance--;
                apply(word, colors);

                if (possibleAnswers.size() <= 10) {
                    System.out.println("Possible Words (" + possibleAnswers.size() + "):");
                    for (Word pos : possibleAnswers) System.out.println("- " + pos);
                }

                if (possibleAnswers.isEmpty()) return;
            }
        }
    }

    // ------------------------------- Core Logic -------------------------------

    private static Word recommendWord(@NotNull Wordlist wordlist) {
        Rater rater = new RaterV2();
        int bestScore = Integer.MIN_VALUE;

        Wordlist bestEliminators = new Wordlist();
        Wordlist answerEliminators = new Wordlist();

        for (Word eliminator : wordlist) {
            int score = rater.rate(condition, eliminator);

            if (score > bestScore) {
                bestScore = score;
                bestEliminators = new Wordlist();
                answerEliminators = new Wordlist();
            }

            if (score == bestScore) {
                bestEliminators.add(eliminator);
                if (possibleAnswers.contains(eliminator)) {
                    answerEliminators.add(eliminator);
                }
            }
        }

        boolean ableToAnswer = !answerEliminators.isEmpty();
        Wordlist eliminators = ableToAnswer ? answerEliminators : bestEliminators;

        Word choice = eliminators.get((int) (Math.random() * eliminators.size()));

        System.out.printf("%s(%d%s : %2d): %s\n",
                rater.getName(), bestScore,
                ableToAnswer ? "+a" : "  ",
                eliminators.size(),
                choice);

        return choice;
    }

    private static void printRecommendation(Word elim, Word ans, int percent) {
        System.out.printf("Random Answer(-?+a : %d): %s%n", possibleAnswers.size(), ans);
        System.out.print("[CHANCES LEFT: " + chance +"] Recommended Word: ");
        System.out.println();

        String status;
        if (possibleAnswers.size() == 1) {
            status = "GOOD";
        } else if (chance <= 1) {
            status = "RAND";
        } else if (possibleAnswers.size() <= chance) {
            status = "SAFE";
        } else {
            System.out.printf("%s (ELIM)%n", elim);
            return;
        }
        boolean useElim = possibleAnswers.contains(elim);
        System.out.print(useElim ? elim : ans);
        System.out.printf(" (%s %s", status, (percent==0?"-0":String.format("%2d%%",percent)));
        if(useElim) System.out.print(" : ELIM+A");
        System.out.println(")");

        System.out.println();
    }

    private static @Nullable Word inputWord(Scanner scanner) {
        try {
            System.out.printf("WORD (%d): ", length);
            Word word = new Word(scanner.nextLine());
            if (word.length != length) return null;
            return word;
        } catch (Exception e) {
            return null;
        }
    }

    private static WordleColor @Nullable [] inputColors(Scanner scanner) {
        try {
            System.out.printf("COLOR(%d): ", length);
            WordleColor[] colors = WordleColor.fromString(scanner.nextLine());
            if (colors.length != length) return null;
            return colors;
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isCorrect(WordleColor[] colors) {
        return Arrays.stream(colors).noneMatch(c -> c == WordleColor.BLACK || c == WordleColor.YELLOW);
    }

    private static void apply(Word word, WordleColor[] colors) {
        condition.apply(word, colors);
        Wordlist newPossibleWords = new Wordlist();
        for (Word posWord : possibleAnswers) {
            if (condition.isPossible(posWord)) {
                newPossibleWords.add(posWord);
            }
        }
        System.out.println("Possible words count: " + possibleAnswers.size() + " -> " + newPossibleWords.size());
        possibleAnswers = newPossibleWords;
        System.out.println();
    }

    // ----------------------------- Wordlist Loader -----------------------------

    private static @NotNull Wordlist loadWordlist(Scanner scanner, String[] args) throws IOException {
        while (true) {
            File wordlistFile = inputWordlistFile(scanner, args);
            try {
                return readWordlist(wordlistFile);
            } catch (IOException e) {
                System.out.println("ERROR: Invalid wordlist file!\n");
            }
        }
    }

    private static @NotNull Wordlist readWordlist(File file) throws IOException {
        Wordlist wordlist = new Wordlist();
        try (WordlistInputStream in = new WordlistInputStream(file)) {
            String word;
            while ((word = in.nextWord()) != null) {
                if (word.length() == length) {
                    wordlist.add(new Word(word));
                }
            }
        }
        return wordlist;
    }

    private static File inputWordlistFile(Scanner scanner, String @NotNull [] args) throws IOException {
        if (args.length == 1) {
            File file = new File(args[0]);
            if (!file.exists()) throw new FileNotFoundException("'" + args[0] + "' does not exist!");
            if (!file.isFile()) throw new IOException("'" + args[0] + "' is not a file!");
            return file;
        }

        File dir = new File("wordlist");
        if (!dir.exists()) dir.mkdir();
        if (!dir.isDirectory()) throw new IOException("'./wordlist' exists but is not a directory!");

        List<File> files;
        while (true) {
            files = Arrays.stream(Objects.requireNonNullElse(dir.listFiles(), new File[0]))
                    .filter(File::isFile).toList();

            System.out.println("Wordlist (" + files.size() + ")");
            for (int i = 0; i < files.size(); i++) {
                System.out.printf("[%d] %s%n", i + 1, files.get(i).getName());
            }

            System.out.print("> ");
            String input = scanner.nextLine();

            for (File f : files) {
                if (input.equals(f.getName())) return f;
            }

            try {
                int idx = Integer.parseInt(input) - 1;
                return files.get(idx);
            } catch (Exception e) {
                System.out.println("Invalid selection!");
            }
        }
    }

    // ---------------------------- Utility Functions ----------------------------

    private static @Nullable Word pickRandom(@NotNull Wordlist words) {
        if (words.isEmpty()) return null;
        return words.get((int) (Math.random() * words.size()));
    }
}
