package com.daar.egrep;

import java.util.*;

public class Automaton implements PatternMatcher {
    private final Map<Integer, Map<Integer, Integer>> states = new HashMap<>();
    private final Map<Integer, List<Integer>> epsilons = new HashMap<>();
    private final Stack<Integer> acceptings = new Stack<>();
    private final Stack<Integer> starts = new Stack<>();
    private final Set<Integer> alphabet = new HashSet<>();

    private int cursor = -1;

    private void addState(int id) {
        states.putIfAbsent(id, new HashMap<>());
        epsilons.putIfAbsent(id, new ArrayList<>());
    }

    private void addStates(Set<Integer> ids) {
        ids.forEach(this::addState);
    }

    @Override
    public ReadingProcessStatus readCharacter(int ch) {
        if (states.get(cursor).containsKey(ch)) {
            cursor = states.get(cursor).get(ch);
            return ReadingProcessStatus.KEEP;
        } else if (states.get(cursor).containsKey(RegExParser.DOT)) {
            cursor = states.get(cursor).get(RegExParser.DOT);
            return ReadingProcessStatus.KEEP;
        }
        return ReadingProcessStatus.FAILED;
    }

    @Override
    public boolean isAccepted() {
        return acceptings.contains(cursor);
    }

    @Override
    public void reset() {
        cursor = starts.peek();
    }

    public Automaton toDFA() {
        Automaton dfa = new Automaton();
        Map<Integer, Set<Integer>> epsilonClosures = new HashMap<>();
        for (Integer state : states.keySet()) {
            epsilonClosures.put(state, epsilonClosure(state));
        }
        Map<Set<Integer>, Integer> dfaStatesMap = new HashMap<>();
        Queue<Set<Integer>> queue = new LinkedList<>();
        Set<Integer> startClosure = new HashSet<>();
        for (Integer start : starts) {
            startClosure.addAll(epsilonClosures.get(start));
        }
        int dfaStateId = 0;
        dfaStatesMap.put(startClosure, dfaStateId++);
        queue.add(startClosure);
        dfa.starts.push(0); // État initial du DFA
        for (Integer nfaState : startClosure) {
            if (acceptings.contains(nfaState)) {
                dfa.acceptings.push(0);
                break;
            }
        }
        while (!queue.isEmpty()) {
            Set<Integer> currentSet = queue.poll();
            int currentDfaStateId = dfaStatesMap.get(currentSet);
            dfa.addState(currentDfaStateId);
            for (int symbol : alphabet) {
                Set<Integer> moveResult = new HashSet<>();
                for (int state : currentSet) {
                    Map<Integer, Integer> transitions = states.get(state);
                    if (transitions != null && transitions.containsKey(symbol)) {
                        int target = transitions.get(symbol);
                        moveResult.addAll(epsilonClosures.get(target));
                    }
                }
                if (moveResult.isEmpty()) continue;
                if (!dfaStatesMap.containsKey(moveResult)) {
                    dfaStatesMap.put(moveResult, dfaStateId);
                    queue.add(moveResult);
                    for (Integer nfaState : moveResult) {
                        if (acceptings.contains(nfaState)) {
                            dfa.acceptings.push(dfaStateId);
                            break;
                        }
                    }
                    dfa.addState(dfaStateId);
                    dfaStateId++;
                }
                int targetDfaStateId = dfaStatesMap.get(moveResult);
                dfa.states.get(currentDfaStateId).put(symbol, targetDfaStateId);
                dfa.alphabet.add(symbol);
            }
        }
        dfa.cursor = dfa.starts.peek();
        return dfa;
    }

    private Set<Integer> epsilonClosure(int state) {
        Set<Integer> closure = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        closure.add(state);
        stack.push(state);
        while (!stack.isEmpty()) {
            int current = stack.pop();
            List<Integer> eps = epsilons.getOrDefault(current, new ArrayList<>());
            for (int next : eps) {
                if (!closure.contains(next)) {
                    closure.add(next);
                    stack.push(next);
                }
            }
        }
        return closure;
    }

    @Override
    public String toString() {
        return "states: " + states
                + "\nepsilons: " + epsilons
                + "\nacceptings: " + acceptings
                + "\nstarts: " + starts
                + "\nalphabet: " + alphabet;
    }

    private static int transformSymbol(RegExTree ret, Automaton current, int start) {
        int end = start + 1;
        current.addStates(Set.of(start, end));
        Map<Integer, Integer> transitions = current.states.get(start);
        transitions.putIfAbsent(ret.root, end);
        current.starts.push(start);
        current.acceptings.push(end);
        current.alphabet.add(ret.root);
        return end + 1;
    }

    private static int transformEtoile(Automaton current, int start) {
        int end = start + 1;
        int r1start = current.starts.pop(), r1end = current.acceptings.pop();
        current.addStates(Set.of(start, end));        ;
        current.epsilons.get(start).add(r1start);
        current.epsilons.get(start).add(end);
        current.epsilons.get(r1end).add(end);
        current.epsilons.get(r1end).add(r1start);
        current.starts.push(start);
        current.acceptings.push(end);
        return end + 1;
    }

    private static int copyAutomaton(Automaton current, int start) {
        int end = start + 1;
        int rstart = current.starts.peek(), rend = current.acceptings.peek();
        current.addStates(Set.of(start, end));
        current.states.get(start).putAll(current.states.get(rstart));
        current.states.get(start).forEach((symbol, state) -> {
            if (state == rend) current.states.get(start).put(symbol, end);
        });
        current.epsilons.get(start).addAll(current.epsilons.get(rstart));
        current.epsilons.get(start).replaceAll(integer -> integer == rend ? end : integer);
        current.starts.push(start);
        current.acceptings.push(end);
        return end + 1;
    }

    private static int transformPlus(Automaton current, int start) {
        int nstate = copyAutomaton(current, start);
        int between = nstate + 1;
        int end = between + 1;
        int r1start = current.starts.pop(), r1end = current.acceptings.pop();
        int r2start = current.starts.pop(), r2end = current.acceptings.pop();
        current.addStates(Set.of(nstate, end, between));
        // Concatene R1 à R2
        current.epsilons.get(nstate).add(r1start);
        current.epsilons.get(r1end).add(between);
        current.epsilons.get(between).add(r2start);
        // Dans le cas, plus qu'un répétition, on fait comme pour étoile
        // Ainsi on capture au moins une fois le motif
        current.epsilons.get(between).add(r1start);
        current.epsilons.get(between).add(end);
        current.epsilons.get(r2end).add(end);
        current.epsilons.get(r2end).add(r2start);

        current.starts.push(start);
        current.acceptings.push(end);
        return end + 1;
    }

    private static void transformConcat(Automaton current) {
        int r1end, r2start, r1start;
        r1start = current.starts.pop();
        r1end = current.acceptings.pop();
        r2start = current.starts.pop();
        current.epsilons.get(r1end).add(r2start);
        current.starts.push(r1start);
    }

    private static int transformAltern(Automaton current, int start) {
        int end = start + 1;
        int r1start, r2start, r1end, r2end;
        r1start = current.starts.pop();
        r1end = current.acceptings.pop();
        r2start = current.starts.pop();
        r2end = current.acceptings.pop();
        current.addStates(Set.of(start, end));
        current.epsilons.get(start).add(r1start);
        current.epsilons.get(start).add(r2start);
        current.epsilons.get(r1end).add(end);
        current.epsilons.get(r2end).add(end);
        current.starts.push(start);
        current.acceptings.push(end);
        return end + 1;
    }

    private static int transformAux(RegExTree ret, Automaton current, int initState) {
        int nstate = initState;
        if (ret.subTrees.isEmpty()) {
            nstate = transformSymbol(ret, current, nstate);
        }
        for (int i = ret.subTrees.size() - 1; i >= 0; i--) {
            nstate = transformAux(ret.subTrees.get(i), current, nstate);
        }
        if (ret.root == RegExParser.ETOILE) {
            nstate = transformEtoile(current, nstate);
        }
        if (ret.root == RegExParser.PLUS) {
            nstate = transformPlus(current, nstate);
        }
        if (ret.root == RegExParser.ALTERN) {
            nstate = transformAltern(current, nstate);
        }
        if (ret.root == RegExParser.CONCAT) {
            transformConcat(current);
        }
        return nstate;
    }

    public static Automaton transform(RegExTree ret) {
        Automaton result = new Automaton();
        int nstate = 0;
        transformAux(ret, result, nstate);
        result.cursor = result.starts.peek();
        return result;
    }
}
