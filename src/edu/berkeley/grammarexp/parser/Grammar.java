package edu.berkeley.grammarexp.parser;

import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 2:54 PM
 */

class Start {
    private String val = "Start";

    @Override
    public String toString() {
        return val;
    }
}

public class Grammar {
    private ArrayList nonTerminals;
    private HashMap<Object, Integer> nonTerminalsToId;


    private ArrayList terminals;
    private HashMap<Object, Integer> terminalsToId;

    private HashMap<Integer,ArrayList<Rule>> rules;


    private boolean[] nullable = null;
    private Set<Integer>[] follow = null;
    private Set<Integer>[] first = null;

    private boolean isInit = false;
    private Rule startRule;

    public Grammar() {
        nonTerminals = new ArrayList();
        nonTerminalsToId = new HashMap<Object, Integer>();
        terminals = new ArrayList();
        terminalsToId = new HashMap<Object, Integer>();
        rules = new HashMap<Integer, ArrayList<Rule>>();
    }

    public int getNonTerminalID(Object name) {
        int start = 0;
        boolean localInit = false;
        if (!isInit) {
            isInit = true;
            localInit = true;
            start = getNonTerminalID(new Start());
        }

        Integer id = nonTerminalsToId.get(name);
        if (id != null) {
            return id;
        }
        int ret = -nonTerminals.size() - 1;
        nonTerminals.add(name);
        nonTerminalsToId.put(name, ret);

        if (localInit) {
            startRule = addProduction(start, ret);
        }
        return ret;
    }

    public Object getSymbolFromID(int id) {
        if (isTerminal(id)) {
            if (id % 2 == 0) {
                return terminals.get(id / 2);
            } else {
                return new Character((char) (id / 2));
            }
        } else {
            id = -id - 1;
            return nonTerminals.get(id);
        }
    }

    public int getTerminalID(Object name) {
        Integer id = terminalsToId.get(name);
        if (id != null) {
            return id;
        }
        int ret = terminals.size() * 2;
        nonTerminals.add(name);
        nonTerminalsToId.put(name, ret);
        return ret;
    }

    public int getTerminalID(char ch) {
        assert ch >= 0;
        return ch * 2 + 1;
    }

//    public Object getSymbolFromID(int id) {
//        if (id % 2 == 0) {
//            return terminals.get(id / 2);
//        } else {
//            return new Character((char) (id / 2));
//        }
//    }

    public boolean isTerminal(int id) {
        return id >= 0;
    }

    public boolean isTerminalCharacter(int id) {
        return id >= 0 && id%2 == 1;
    }

    private void addRule(Rule rule) {
        int lhs = rule.getLHS();
        ArrayList<Rule> ruleList = rules.get(lhs);
        if (ruleList == null) {
            ruleList = new ArrayList<Rule>();
            rules.put(lhs, ruleList);
        }
        ruleList.add(rule);
    }

    public Rule addProduction(int lhs, Object... symbols) {
        int len = symbols.length, nsyms = 0, i;
        Rule ret2;
        for (i = 0; i < len; i++) {
            if (symbols[i] instanceof String) {
                nsyms += ((String) symbols[i]).length();
            } else if (symbols[i] instanceof Integer) {
                nsyms++;
            } else {
                throw new RuntimeException("Unknown symbolc type in addProduction: " + symbols[i]);
            }
        }
        RuleRHS ret = new RuleRHS(nsyms, this);
        for (i = 0; i < len; i++) {
            if (symbols[i] instanceof String) {
                for (char ch : ((String) symbols[i]).toCharArray()) {
                    ret.addSymbol(getTerminalID(ch));
                }
            } else if (symbols[i] instanceof Integer) {
                ret.addSymbol(((Integer) symbols[i]).intValue());
            } else {
                throw new RuntimeException("Unknown symbolc type in addProduction: " + symbols[i]);
            }
        }
        addRule(ret2 = new Rule(lhs, ret, this));
        return ret2;
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


    public void computeFirstFollowSets() {
        if (first != null) return;

        first = new HashSet[nonTerminals.size()];
        follow = new HashSet[nonTerminals.size()];
        nullable = new boolean[nonTerminals.size()];

        int i;
        for (i = 0; i < first.length; i++) {
            first[i] = new HashSet<Integer>();
            follow[i] = new HashSet<Integer>();
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Integer lhs: rules.keySet()) {
                for (Rule r : rules.get(lhs)) {
                    int X = getNonTerminalIndex(r.getLHS());
                    RuleRHS y = r.getRHS();

                    int k = y.length();
                    boolean allNullable = true;
                    for (i = 0; i < k; i++) {
                        int Yi = y.get(i);
                        if (allNullable) {
                            if (isTerminal(Yi)) {
                                changed = add(first[X], Yi, changed);
                                allNullable = false;
                            } else {
                                int Yidx = getNonTerminalIndex(Yi);
                                changed = add(first[X], first[Yidx], changed);
                                if (!nullable[Yidx]) {
                                    allNullable = false;
                                }
                            }
                        }
                        int Yidx = getNonTerminalIndex(Yi);
                        boolean postNullable = true;
                        if (!isTerminal(Yi)) {
                            for (int j = i + 1; j < k; j++) {
                                int Yj = y.get(j);
                                if (postNullable) {
                                    if (isTerminal(Yj)) {
                                        changed = add(follow[Yidx], Yj, changed);
                                        postNullable = false;
                                        break;
                                    } else {
                                        int Yjdx = getNonTerminalIndex(Yj);
                                        changed = add(follow[Yidx], first[Yjdx], changed);
                                        if (!nullable[Yjdx]) {
                                            postNullable = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (postNullable) {
                                changed = add(follow[Yidx], follow[X], changed);
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
        int len = nonTerminals.size();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i<len; i++) {
            sb.append(nonTerminals.get(i));
            sb.append(": First = {");
            for(Integer t: first[i]) {
                sb.append(getSymbolFromID(t));
                sb.append(",");
            }
            sb.append("}  Follow = {");
            for(Integer t: follow[i]) {
                sb.append(getSymbolFromID(t));
                sb.append(",");
            }
            sb.append("}  Nullable = ");
            sb.append(nullable[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer lhs: rules.keySet()) {
            for (Rule r : rules.get(lhs)) {
                sb.append(r.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public List<Rule> getRules(int nonTerminal) {
        return rules.get(nonTerminal);
    }

    public LRDFA generateLR0() {
        ItemSet I0 = new ItemSet(this);
        I0.add(new Item(startRule));
        LRDFA ret = new LRDFA(I0, this);
        ret.build();
        return ret;
    }
}
