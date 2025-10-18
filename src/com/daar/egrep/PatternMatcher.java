package com.daar.egrep;

public interface PatternMatcher {
    ReadingProcessStatus readCharacter(int c);
    boolean isAccepted();
    void reset();
}
