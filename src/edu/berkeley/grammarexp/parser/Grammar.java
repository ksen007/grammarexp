package edu.berkeley.grammarexp.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 2:54 PM
 */

public class Grammar {
    final public static String LB = "{{";
    final public static String RB = "}}";
    private ArrayList nonTerminals;
    private ArrayList<Boolean> isHiddenNonTerminals;
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
        isHiddenNonTerminals = new ArrayList<Boolean>();
        nonTerminalsToId = new HashMap<Object, Integer>();
        terminals = new ArrayList();
        terminalsToId = new HashMap<Object, Integer>();
        rules = new ArrayList<ArrayList<Rule>>();
    }

    private int getNonTerminalID(Object name, boolean hidden) {
        int start = 0;
        boolean localInit = false;
        if (!isInit) {
            isInit = true;
            localInit = true;
            startNonTerminal = start = getNonTerminalID(new NonTerminal("Start"), true);
            endToken = getTerminalID(new Token("$"));
        }

        Integer id = nonTerminalsToId.get(name);
        if (id != null) {
            return id;
        }
        int ret = nonTerminals.size();
        nonTerminals.add(name);
        isHiddenNonTerminals.add(hidden);
        nonTerminalsToId.put(name, ret);
        rules.add(new ArrayList<Rule>());

        if (localInit) {
            startRule = addProduction(start, ret);
        }
        return ret;
    }

    public int getNonTerminalID(Object name) {
        return getNonTerminalID(name, false);
    }

    public int getHiddenNonTerminalID(Object name) {
        return getNonTerminalID(name, true);
    }

    private int getTerminalID(Object name, boolean isHidden) {
        Integer id = terminalsToId.get(name);
        if (id != null) {
            return id;
        }
        int offset = isHidden? 1: 2;
        int ret = -terminals.size() * 3 - offset;
        terminals.add(name);
        terminalsToId.put(name, ret);
        return ret;
    }

    public int getTerminalID(char ch) {
        assert ch >= 0;
        return -ch * 3;
    }

    public int getTerminalID(Object name) {
        return getTerminalID(name, true);
    }

    public int getVisibleTerminalID(Object name) {
        return getTerminalID(name, false);
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
            if (id % 3 == 0) {
                return new Character((char) (id / 3));
            } else {
                return terminals.get(id / 3);
            }
        } else {
            return nonTerminals.get(id);
        }
    }

    public boolean getHiddenFromID(int id) {
        if (isTerminal(id)) {
            id = -id;
            return !(id % 3 == 2);
        } else {
            return isHiddenNonTerminals.get(id);
        }
    }

    public boolean isTerminal(int id) {
        return id < 0;
    }

    public boolean isTerminalCharacter(int id) {
        return id < 0 && (-id) % 3 == 0;
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
        for (ArrayList<Rule> ruleList : rules) {
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
        //System.out.println(lalr.toString());
        table = lalr.getLALRTables();
    }


    private LinkedList parseAux(Scanner scanner) throws IOException {
        LRStack stack = new LRStack();
        stack.push("", 0);
        int token;
        token = scanner.nextToken();

        while (true) {
            Integer state = stack.topState();
            Integer nextState = table.GOTO.get(state).get(token);
            if (nextState != null) {
                stack.push(scanner.tokenValue.toString(), nextState);
                token = scanner.nextToken();
            } else {
                ArrayList<Rule> rls = table.ACTION.get(state).get(token);
                if (rls == null || rls.isEmpty()) {
                    scanner.close();
                    throw new ParsingException("Parsing failed: not expecting " + scanner.tokenValue);
                } else {
                    Rule rl = rls.get(0);
                    int X = rl.getLHS();
                    int len = rl.getRHS().length();
                    LinkedList processed = stack.popn(len);
                    if (!getHiddenFromID(X)) {
                        processed.addFirst(" ");
                        processed.addFirst(getSymbolFromID(X));
                        processed.addFirst(LB);
                        processed.addLast(RB);
                    }
                    if (X == startNonTerminal) {
                        if ((token = scanner.nextToken()) != endToken) {
                            scanner.close();
                            throw new ParsingException("Parsing failed: more characters left " + scanner.tokenValue);
                        } else {
                            scanner.close();
                            return processed;
                        }
                    }
                    stack.push(processed, table.GOTO.get(stack.topState()).get(X));
                }
            }
        }
    }

    public String parse(Scanner scanner) throws IOException {
        LinkedList ast = parseAux(scanner);
        LinkedList dfs = new LinkedList();
        String prev = "";
        StringBuilder sb = new StringBuilder();
        dfs.addFirst(ast);
        while(!dfs.isEmpty()) {
            Object node = dfs.removeFirst();
            if (node instanceof LinkedList) {
                Collections.reverse((LinkedList)node);
                for(Object child: (LinkedList)node) {
                    dfs.addFirst(child);
                }
            } else {
                String tmp = prev + node;
                if (tmp.equals(LB) || tmp.equals(RB)) {
                    sb.append(LB);
                    sb.append(prev);
                    prev = "";
                } else {
                    sb.append(prev);
                    prev = node.toString();
                }
            }
        }
        if (!prev.equals("")) {
            sb.append(prev);
        }
        return sb.toString();
    }

    public String parse(String inp) throws IOException {
        return parse(new CharStreamScanner(this, new InputStreamReader(new ByteArrayInputStream(inp.getBytes()))));
    }

}
