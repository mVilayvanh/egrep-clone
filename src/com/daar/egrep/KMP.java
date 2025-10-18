package com.daar.egrep;

import java.util.Objects;

public class KMP implements PatternMatcher {

    private final int[] t;

    public KMP(String w) {
        Objects.requireNonNull(w);
        if (w.matches("[a-zA-Z]+")) throw new IllegalArgumentException("Must not contain symbol");
        this.t = new int[w.length() + 1];
        int m = 0, i = 0;
    }

    @Override
    public ReadingProcessStatus readCharacter(int c) {
        return null;
    }

    @Override
    public boolean isAccepted() {
        return false;
    }

    @Override
    public void reset() {

    }
}
