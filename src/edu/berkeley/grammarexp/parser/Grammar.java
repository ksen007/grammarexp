package edu.berkeley.grammarexp.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 2:54 PM
 */

public class Grammar {
    private ArrayList nonTerminals;
    private HashMap<Object, Integer> nonTerminalsToId;


    private ArrayList terminals;
    private HashMap<Object, Integer> terminalsToId;

    private int nRules = 0;

    private ArrayList<ArrayList<Rule>> rules;

    private boolean isInit = false;
    private Rule startRule;
    private int startNonTerminal;
    private LALRTables table;
    public int endToken;

    public Grammar() {
        nonTerminals = new ArrayList();
        nonTerminalsToId = new HashMap<Object, Integer>();
        terminals = new ArrayList();
        terminalsToId = new HashMap<Object, Integer>();
        rules = new ArrayList<ArrayList<Rule>>();
    }

    public int getNonTerminalID(Object name) {
        int start = 0;
        boolean localInit = false;
        if (!isInit) {
            isInit = true;
            localInit = true;
            startNonTerminal = start = getNonTerminalID(new NonTerminal("Start"));
            endToken = getTerminalID(new Token("$"));
        }

        Integer id = nonTerminalsToId.get(name);
        if (id != null) {
            return id;
        }
        int ret = nonTerminals.size();
        nonTerminals.add(name);
        nonTerminalsToId.put(name, ret);
        rules.add(new ArrayList<Rule>());

        if (localInit) {
            startRule = addProduction(start, ret);
        }
        return ret;
    }

    public int getTerminalID(Object name) {
        Integer id = terminalsToId.get(name);
        if (id != null) {
            return id;
        }
        int ret = -terminals.size() * 2 - 2;
        terminals.add(name);
        terminalsToId.put(name, ret);
        return ret;
    }

    public int getTerminalID(char ch) {
        assert ch >= 0;
        return - ch * 2 - 1;
    }

    public Rule getStartRule() {
        return startRule;
    }

    public int getNonTerminalsCount() {
        return nonTerminals.size();
    }

    public Object getSymbolFromID(int id) {
        if (isTerminal(id)) {
            id = -id;
            if (id % 2 == 0) {
                return terminals.get(id / 2 - 1);
            } else {
                return new Character((char) (id / 2));
            }
        } else {
            return nonTerminals.get(id);
        }
    }

    public boolean isTerminal(int id) {
        return id < 0;
    }

    public boolean isTerminalCharacter(int id) {
        return id < 0 && (-id) % 2 == 1;
    }

    private void addRule(Rule rule) {
        int lhs = rule.getLHS();
        ArrayList<Rule> ruleList = rules.get(lhs);
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
        addRule(ret2 = new Rule(lhs, ret, nRules, this));
        nRules++;
        return ret2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<Rule> ruleList: rules) {
            for (Rule r : ruleList) {
                sb.append(r.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public List<Rule> getRules(int nonTerminal) {
        return rules.get(nonTerminal);
    }

    public void compile() {
        LALR lalr = LALR.generateLALRTables(this);
        System.out.println(lalr.toString());
        table = lalr.getLALRTables();
    }



    public String parse(InputStreamReader in) throws IOException {
        LRStack stack = new LRStack(this);
        stack.push("",0);
        int inp = in.read();

        while (true) {
            int token;
            if (inp == -1) {
                token = endToken;
            } else {
                token = getTerminalID((char) inp);
            }
            Integer state = stack.topState();
            Integer nextState = table.GOTO.get(state).get(token);
            if (nextState!=null) {
                stack.push(token, nextState);
                inp = in.read();
            } else {
                ArrayList<Rule> rls = table.ACTION.get(state).get(token);
                if (rls == null || rls.isEmpty()) {
                    in.close();
                    throw new ParsingException("Parsing failed: not expecting "+(char)inp);
                } else {
                    Rule rl = rls.get(0);
                    int X = rl.getLHS();
                    int len = rl.getRHS().length();
                    String mod = stack.popn(len);
                    mod = LRStack.LB + " " + getSymbolFromID(X)+ " "+mod + " "+LRStack.RB;
                    if (X == startNonTerminal) {
                        if ((inp = in.read()) != -1) {
                            in.close();
                            throw new ParsingException("Parsing failed: more characters left "+(char)inp);
                        } else {
                            in.close();
                            return mod;
                        }
                    }
                    stack.push(mod, table.GOTO.get(stack.topState()).get(X));
                }
            }
        }
    }

}
