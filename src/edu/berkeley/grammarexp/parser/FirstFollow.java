package edu.berkeley.grammarexp.parser;

import java.util.Set;
import java.util.TreeSet;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 8:54 AM
 */
public class FirstFollow {
    private Grammar g;
    private boolean[] nullable = null;
    private Set<Integer>[] follow = null;
    private Set<Integer>[] first = null;


    public FirstFollow(Grammar g) {
        this.g = g;
    }

    private int getNonTerminalIndex(int id) {
        return -id - 1;
    }

    private boolean add(Set<Integer> to, Set<Integer> from, boolean changed) {
        int sz = to.size();
        to.addAll(from);
        if (to.size() > sz) return true;
        else return changed;
    }

    private boolean add(Set<Integer> to, int from, boolean changed) {
        int sz = to.size();
        to.add(from);
        if (to.size() > sz) return true;
        else return changed;
    }


    public boolean isFirstFollowSetsComputed() {
        return first != null;
    }

    public void computeFirstFollowSets(boolean computeFollow) {
        if (first != null) return;

        int nNonTerminals = g.getNonTerminalsCount();

        first = new TreeSet[nNonTerminals];
        follow = new TreeSet[nNonTerminals];
        nullable = new boolean[nNonTerminals];

        int i;
        for (i = 0; i < first.length; i++) {
            first[i] = new TreeSet<Integer>();
            follow[i] = new TreeSet<Integer>();
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int lhs = 0; lhs < nNonTerminals; lhs++) {
                for (Rule r : g.getRules(lhs)) {
                    int X = r.getLHS();
                    RuleRHS y = r.getRHS();

                    int k = y.length();
                    boolean allNullable = true;
                    for (i = 0; i < k; i++) {
                        int Yi = y.get(i);
                        if (allNullable) {
                            if (g.isTerminal(Yi)) {
                                changed = add(first[X], Yi, changed);
                                allNullable = false;
                            } else {
                                changed = add(first[X], first[Yi], changed);
                                if (!nullable[Yi]) {
                                    allNullable = false;
                                }
                            }
                            if (!computeFollow && !allNullable) {
                                break;
                            }
                        }
                        if (computeFollow) {
                            boolean postNullable = true;
                            if (!g.isTerminal(Yi)) {
                                for (int j = i + 1; j < k; j++) {
                                    int Yj = y.get(j);
                                    if (postNullable) {
                                        if (g.isTerminal(Yj)) {
                                            changed = add(follow[Yi], Yj, changed);
                                            postNullable = false;
                                            break;
                                        } else {
                                            changed = add(follow[Yi], first[Yj], changed);
                                            if (!nullable[Yj]) {
                                                postNullable = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (postNullable) {
                                    changed = add(follow[Yi], follow[X], changed);
                                }
                            }
                        }
                    }
                    if (allNullable) {
                        boolean old = nullable[X];
                        nullable[X] = true;
                        if (!old)
                            changed = true;
                    }
                }
            }
        }
    }


    public String getFirstFollowAsString() {
        int len = g.getNonTerminalsCount();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            sb.append(g.getSymbolFromID(i));
            sb.append(": First = {");
            boolean firstF = true;
            for (Integer t : first[i]) {
                if (firstF) {
                    firstF = false;
                } else {
                    sb.append(", ");
                }
                sb.append(g.getSymbolFromID(t));
            }
            sb.append("}  Follow = {");
            firstF = true;
            for (Integer t : follow[i]) {
                if (firstF) {
                    firstF = false;
                } else {
                    sb.append(", ");
                }
                sb.append(g.getSymbolFromID(t));
            }
            sb.append("}  Nullable = ");
            sb.append(nullable[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    public Set<Integer> getFirst(int nonTerminalId) {
        return first[nonTerminalId];
    }

    public boolean getNullable(int nonTerminalId) {
        return nullable[nonTerminalId];
    }


}
