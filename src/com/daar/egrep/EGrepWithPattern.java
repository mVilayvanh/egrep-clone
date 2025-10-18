package com.daar.egrep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public class EGrepWithPattern {

    public static void main(String[] args) {
        var pattern = Pattern.compile(args[0]);
        var filepath = Paths.get(args[1]);
        LinkedHashSet<String> result =  new LinkedHashSet<>();
        try {
            int lineCount = 1;
            for (String line : Files.readAllLines(filepath)) {
                if (pattern.matcher(line).find()) result.add(lineCount + ":" + line);
                lineCount++;
            }
            System.out.println(String.join("\n", result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
