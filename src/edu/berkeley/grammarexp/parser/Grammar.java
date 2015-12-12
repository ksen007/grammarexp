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

class Precedence {
    int precedence;
    boolean isAssociative;
    boolean rightAssociative;

    public Precedence(int precedence, boolean isAssociative, boolean rightAssociative) {
        this.precedence = precedence;
        this.isAssociative = isAssociative;
        this.rightAssociative = rightAssociative;
    }

}

public class Grammar {
    final public static String LB = "{{";
    final public static String RB = "}}";
    private ArrayList nonTerminals;
    private ArrayList<Boolean> isHiddenNonTerminals;
    private HashMap<Object, Integer> nonTerminalsToId;


    private ArrayList terminals;
    private HashMap<Object, Integer> terminalsToId;
    private HashMap<Integer, Precedence> precedence;


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
        precedence = new HashMap<Integer, Precedence>();
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
        int offset = isHidden ? 1 : 2;
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

    int prec = 0;

    public void addPrecedenceAsPrevious(int terminalId, boolean isAssociative, boolean isRight) {
        precedence.put(terminalId, new Precedence(prec, isAssociative, isRight));
    }

    public void addPrecedence(int terminalId, boolean isAssociative, boolean isRight) {
        prec++;
        addPrecedenceAsPrevious(terminalId, isAssociative, isRight);
    }

    public void addPrecedenceAsPrevious(String token, boolean isAssociative, boolean isRight) {
        if (token.length() != 1) throw new RuntimeException("Token must have one character");
        precedence.put(getTerminalID(token.charAt(0)), new Precedence(prec, isAssociative, isRight));
    }

    public void addPrecedence(String token, boolean isAssociative, boolean isRight) {
        prec++;
        addPrecedenceAsPrevious(token, isAssociative, isRight);
    }

    public void addPrecedenceSameAs(Rule rule, int terminalID) {
        rule.getRHS().setPrecendence(getPrecedence(terminalID));
    }

    public Precedence getPrecedence(int terminalId) {
        return precedence.get(terminalId);
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
                throw new RuntimeException("Unknown symbolic type in addProduction: " + symbols[i]);
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
                throw new RuntimeException("Unknown symbolic type in addProduction: " + symbols[i]);
            }
        }
        addRule(ret2 = new Rule(lhs, ret, nRules, !getHiddenFromID(lhs), this));
        nRules++;
        return ret2;
    }


    public Rule addProductionVisible(int lhs, Object... symbols) {
        Rule ret = addProduction(lhs, symbols);
        ret.setVisible(true);
        return ret;
    }

    public Rule addProductionHidden(int lhs, Object... symbols) {
        Rule ret = addProduction(lhs, symbols);
        ret.setVisible(false);
        return ret;
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


    private ASTNode parseAux(Scanner scanner) throws IOException {
        LRStack stack = new LRStack();
        stack.push(new ASTNodeLeaf(0, "", false), 0);
        int token;
        token = scanner.nextToken();

        while (true) {
            Integer state = stack.topState();
            Integer nextState = table.GOTO.get(state).get(token);
            if (nextState != null) {
                stack.push(new ASTNodeLeaf(scanner.tokenID, scanner.tokenValue, getHiddenFromID(scanner.tokenID)), nextState);
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
                    ASTNodeInternal processed = stack.popn(len, X, rl.isVisible());
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
        ASTNode ast = parseAux(scanner);
        LinkedList dfs = new LinkedList();
        String prev = "";
        StringBuilder sb = new StringBuilder();
        dfs.addFirst(ast);
        while (!dfs.isEmpty()) {
            Object e = dfs.removeFirst();
            if (e instanceof String) {
                sb.append(e);
            } else {
                ASTNode node = (ASTNode)e;
                if (!node.isLeaf()) {
                    LinkedList<ASTNode> children = new LinkedList<ASTNode>(node.getChildren());
                    Collections.reverse(children);
                    if (node.isVisible) {
                        dfs.addFirst(RB);
                    }
                    for (ASTNode child : children) {
                        dfs.addFirst(child);
                    }
                    if (node.isVisible) {
                        dfs.addFirst(getSymbolFromID(node.ID).toString() + " ");
                        dfs.addFirst(LB);
                    }
                } else {
                    sb.append(node.getValue().toString());
//                    String tmp = prev + node;
//                    if (tmp.equals(LB) || tmp.equals(RB)) {
//                        sb.append(LB);
//                        sb.append(prev);
//                        prev = "";
//                    } else {
//                        sb.append(prev);
//                        prev = node.toString();
//                    }
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
