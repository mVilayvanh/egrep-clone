package com.daar.egrep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.LinkedHashSet;
import java.util.Set;
/**
 * EGrep effectue le job cloné du egrep UNIX. Le résultat peut soit être affiché, soit être collecté
 */
public class EGrep {
    private final Path filepath;
    private final String pattern;

    public EGrep(String pattern, String filepath) {
        this.filepath = Paths.get(filepath);
        this.pattern = pattern;
    }

    private Automaton buildAutomaton() {
        try {
            RegExParser rep = new RegExParser(pattern);
            RegExTree ret = rep.parse();
            RegExTreeParser rtp =  new RegExTreeParser(ret);
            return rtp.parse().toDFA();
        } catch (Exception e) {
            throw new RuntimeException("EGrep: " + pattern + ": Wrong pattern given");
        }
    }

    /**
     * Parcourt le texte de filepath et affiche toutes les lignes ayant matché avec le motif fourni
     *
     */
    public void runAndPrint() {
        try {
            LinkedHashSet<String> result = new LinkedHashSet<>();
            Automaton automaton = buildAutomaton();
            int lineCount = 1;
            // Est O(n) avec n la longueur du texte car on parcourt chaque caractère de chaque ligne
            for (String line : Files.readAllLines(filepath)) {
                for (char c : line.toCharArray()) {
                    switch (automaton.readCharacter(c)) {
                        case KEEP -> {
                            if (automaton.isAccepted()) {
                                result.add(lineCount + ":" + line);
                                automaton.reset();
                            }
                        }
                        case FAILED -> automaton.reset();
                    }
                }
                lineCount++;
            }
            System.out.println(String.join("\n", result));
        } catch (IOException e) {
            throw new RuntimeException("EGrep: " + filepath + ": No such file or directory");
        }
    }

    /**
     * Parcourt le texte et collecte les lignes où le motif apparaît.
     *
     * @return L'ensemble des lignes qui ont matché
     */
    public Set<String> runAndCollect() {
        try {
            Set<String> result = new LinkedHashSet<>();
            Automaton a = buildAutomaton();
            for (String line : Files.readAllLines(filepath)) {
                for (char c : line.toCharArray()) {
                    switch (a.readCharacter(c)) {
                        case KEEP -> {
                            if (a.isAccepted()) {
                                result.add(line);
                                a.reset();
                            }
                        }
                        case FAILED -> a.reset();
                    }
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("EGrep: " + filepath + ": No such file or directory");
        }

    }
}
