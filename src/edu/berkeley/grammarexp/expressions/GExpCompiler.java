package edu.berkeley.grammarexp.expressions;

import edu.berkeley.grammarexp.parser.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/6/16
 * Time: 9:04 AM
 */
public class GExpCompiler {
    Grammar g;
    StreamTokenizer scanner;
    State nfa;
    State matchstate;
    int nStates = 0;

    /* http://stackoverflow.com/questions/265457/regex-grammar/265467#265467 */

    public GExpCompiler(String exp) throws IOException {
        g = new Grammar();
        scanner = new StreamTokenizer(g);

        final int GE = g.getNonTerminalID("GE");
        final int Balanced = g.getNonTerminalID("Balanced");
        final int character = g.getHiddenNonTerminalID("Character");
        int CharacterClass = g.getHiddenNonTerminalID("CharacterClass");
        int CompoundCharacter = g.getHiddenNonTerminalID("CompoundCharacter");

        int LB = g.getTerminalID(g.LB); scanner.addString(LB);
        int RB = g.getTerminalID(g.RB); scanner.addString(RB);
        int LR = g.getTerminalID("\\O"); scanner.addString(LR);
        int LPAREN = g.getTerminalID("\\("); scanner.addString(LPAREN);
        int RPAREN = g.getTerminalID("\\)"); scanner.addString(RPAREN);
        int LCURLY = g.getTerminalID("\\{");  scanner.addString(LCURLY);
        int RCURLY = g.getTerminalID("\\}");  scanner.addString(RCURLY);
        int LSQUARE = g.getTerminalID("\\[");  scanner.addString(LSQUARE);
        int RSQUARE = g.getTerminalID("\\]");  scanner.addString(RSQUARE);
        int PIPE = g.getTerminalID("\\|");  scanner.addString(PIPE);
        int STAR = g.getTerminalID("\\*");  scanner.addString(STAR);
        int PLUS = g.getTerminalID("\\+");  scanner.addString(PLUS);
        int QUESTION = g.getTerminalID("\\?");  scanner.addString(QUESTION);
        int PERIOD = g.getTerminalID("\\.");  scanner.addString(PERIOD);
        int ESCAPEDLB = g.getTerminalID(g.LB + g.ESCAPE);  scanner.addString(ESCAPEDLB);
        int ESCAPEDRB = g.getTerminalID(g.ESCAPE + g.RB);  scanner.addString(ESCAPEDRB);
        int PERCENT = g.getTerminalID("\\%");  scanner.addString(PERCENT);

        int WORD = g.getTerminalID("\\w");   scanner.addString(WORD);
        int NOTWORD = g.getTerminalID("\\W");  scanner.addString(NOTWORD);
        int SPACE = g.getTerminalID("\\s");  scanner.addString(SPACE);
        int NOTSPACE = g.getTerminalID("\\S");  scanner.addString(NOTSPACE);
        int DIGIT = g.getTerminalID("\\d");  scanner.addString(DIGIT);
        int NOTDIGIT = g.getTerminalID("\\D");  scanner.addString(NOTDIGIT);


        g.addPrecedence("|", true, false);

        int concat = g.getTerminalID(new Token("."));
        g.addPrecedence(concat, true, false);

        g.addPrecedence("+", true, false);
        g.addPrecedenceAsPrevious("*", true, false);
        g.addPrecedenceAsPrevious("?", true, false);
        g.addPrecedence(g.ESCAPE, true, false);

        g.addProduction(GE, "(", GE, ")");
        Rule C = g.addProduction(GE, GE, GE);
        g.addProduction(GE, GE, "|", GE);
        g.addProduction(GE, GE, "*");
        g.addProduction(GE, GE, "+");
        g.addProduction(GE, GE, "?");
        g.addProduction(GE, character);
        g.addProduction(GE, CharacterClass);
        g.addProduction(GE, CompoundCharacter);
        g.addProduction(GE, Balanced);

        g.addProduction(Balanced, LB, GE, RB);
        g.addProduction(Balanced, LR);

        g.addProduction(CompoundCharacter, PIPE);
        g.addProduction(CompoundCharacter, STAR);
        g.addProduction(CompoundCharacter, PLUS);
        g.addProduction(CompoundCharacter, QUESTION);
        g.addProduction(CompoundCharacter, LPAREN);
        g.addProduction(CompoundCharacter, RPAREN);
        g.addProduction(CompoundCharacter, LSQUARE);
        g.addProduction(CompoundCharacter, RSQUARE);
        g.addProduction(CompoundCharacter, LCURLY);
        g.addProduction(CompoundCharacter, RCURLY);
        g.addProduction(CompoundCharacter, PERIOD);
        g.addProduction(CompoundCharacter, PERCENT);
        g.addProduction(CompoundCharacter, ESCAPEDLB);
        g.addProduction(CompoundCharacter, ESCAPEDRB);

        g.addProduction(CharacterClass, WORD);
        g.addProduction(CharacterClass, NOTWORD);
        g.addProduction(CharacterClass, DIGIT);
        g.addProduction(CharacterClass, NOTDIGIT);
        g.addProduction(CharacterClass, SPACE);
        g.addProduction(CharacterClass, NOTSPACE);
        g.addProduction(CharacterClass, ".");


        g.addProduction(character, "\n");
        g.addProduction(character, "\r");
        g.addProduction(character, "\f");
        g.addProduction(character, "\t");
        g.addProduction(character, " ");

        g.addProduction(character, "#");
        g.addProduction(character, "$");
        g.addProduction(character, "&");
        g.addProduction(character, "'");
        g.addProduction(character, ",");
        g.addProduction(character, "-");
        g.addProduction(character, "/");
        g.addProduction(character, ":");
        g.addProduction(character, ";");
        g.addProduction(character, "<");
        g.addProduction(character, "=");
        g.addProduction(character, ">");
        g.addProduction(character, "@");
        g.addProduction(character, "\"");
        g.addProduction(character, "^");
        g.addProduction(character, "_");
        g.addProduction(character, "`");
        g.addProduction(character, "~");
        g.addProduction(character, "0");
        g.addProduction(character, "1");
        g.addProduction(character, "2");
        g.addProduction(character, "3");
        g.addProduction(character, "4");
        g.addProduction(character, "5");
        g.addProduction(character, "6");
        g.addProduction(character, "7");
        g.addProduction(character, "8");
        g.addProduction(character, "9");
        g.addProduction(character, "A");
        g.addProduction(character, "B");
        g.addProduction(character, "C");
        g.addProduction(character, "D");
        g.addProduction(character, "E");
        g.addProduction(character, "F");
        g.addProduction(character, "G");
        g.addProduction(character, "H");
        g.addProduction(character, "I");
        g.addProduction(character, "J");
        g.addProduction(character, "K");
        g.addProduction(character, "L");
        g.addProduction(character, "M");
        g.addProduction(character, "N");
        g.addProduction(character, "O");
        g.addProduction(character, "P");
        g.addProduction(character, "Q");
        g.addProduction(character, "R");
        g.addProduction(character, "S");
        g.addProduction(character, "T");
        g.addProduction(character, "U");
        g.addProduction(character, "V");
        g.addProduction(character, "W");
        g.addProduction(character, "X");
        g.addProduction(character, "Y");
        g.addProduction(character, "Z");
        g.addProduction(character, "a");
        g.addProduction(character, "b");
        g.addProduction(character, "c");
        g.addProduction(character, "d");
        g.addProduction(character, "e");
        g.addProduction(character, "f");
        g.addProduction(character, "g");
        g.addProduction(character, "h");
        g.addProduction(character, "i");
        g.addProduction(character, "j");
        g.addProduction(character, "k");
        g.addProduction(character, "l");
        g.addProduction(character, "m");
        g.addProduction(character, "n");
        g.addProduction(character, "o");
        g.addProduction(character, "p");
        g.addProduction(character, "q");
        g.addProduction(character, "r");
        g.addProduction(character, "s");
        g.addProduction(character, "t");
        g.addProduction(character, "u");
        g.addProduction(character, "v");
        g.addProduction(character, "w");
        g.addProduction(character, "x");
        g.addProduction(character, "y");
        g.addProduction(character, "z");


        g.addPrecedenceSameAs(C, concat);

        g.compile();
        ASTNode ast = g.parseToAST(exp);
        final Stack<Fragment> stack = new Stack<Fragment>();

        ast.visitPostOrder(new ASTVisitor() {
            Object lastToken;

            @Override
            public void visitInternalNodeBefore(int id, LinkedList<ASTNode> list) {

            }

            @Override
            public void visitInternalNodeAfter(int id, LinkedList<ASTNode> list) {
                Fragment tmp;
                if (id == character) {
                    stack.push(Fragment.literal((Character)lastToken));
                }
                if (list.size()>=2 && id == GE) {
                    ASTNode opnode = list.get(1);
                    if (opnode.isLeaf()) {
                        char op = (Character)opnode.getValue();
                        switch (op) {
                            case '|':
                                tmp = stack.pop();
                                stack.push(Fragment.alternate(stack.pop(), tmp));
                                break;
                            case '*':
                                stack.push(Fragment.star(stack.pop()));
                                break;
                            case '+':
                                stack.push(Fragment.plus(stack.pop()));
                                break;
                            case '?':
                                stack.push(Fragment.question(stack.pop()));
                                break;
                        }
                    }
                    if (list.size()==2 && list.getFirst().getID() == GE && opnode.getID() == GE) {
                        tmp = stack.pop();
                        stack.push(Fragment.concatenate(stack.pop(), tmp));
                    }
                }
            }

            @Override
            public void visitLeafNode(int id, Object value) {
                lastToken = value;
            }
        });

        Fragment f = stack.pop();
        this.matchstate = new State(0, null, null, true, false);
        for(DanglingState ds: f.out) {
            ds.patch(matchstate);
        }
        this.nfa = f.start;
        this.nStates = State.n;
        State.n = 0;
    }

    public String parse(String exp) throws IOException {
        return g.parse(exp);
    }


    int listid = 0;
    StateList clist = null, nlist = null;

    private boolean isMatch(StateList l) {
        for (int i=0; i<l.n; i++) {
            if (l.s[i] == matchstate) {
                return true;
            }
        }
        return false;
    }

    private void addState(StateList l, State s) {
        if (s == null || s.lastlist == listid) {
            return;
        } else {
            s.lastlist = listid;
            if (s.isSplit) {
                addState(l, s.out);
                addState(l, s.out1);
                return;
            }
            l.s[l.n++] = s;
        }
    }

    private StateList startList(State s, StateList l) {
        listid++;
        l.n = 0;
        addState(l, s);
        return l;
    }

    private void step(StateList clist, int c, StateList nlist) {
        State s;
        listid++;
        nlist.n = 0;
        for (int i=0; i<clist.n; i++) {
            s = clist.s[i];
            if (s.c == c)
                addState(nlist, s.out);
        }
    }

    public boolean match(Scanner inp) throws IOException {
        StateList tmp;
        if (clist == null) {
            clist = new StateList(nStates);
            nlist = new StateList(nStates);
        }
        listid = 0;
        clist = startList(nfa, clist);
        while(inp.nextToken() != g.endToken) {
            step(clist, (Character)inp.tokenValue, nlist);
            tmp = clist; clist = nlist; nlist = tmp;
        }
        return isMatch(clist);
    }

    public boolean match(String inp) throws IOException {
        Reader stream = new StringReader(inp);
        scanner.setStream(stream);
        return match(scanner);
    }

    /*
    actor = atom

             atom metacharacter

    atom = character

           .

           ( expression )

           [ characterclass ]

           [ ^ characterclass ]

           { min }

           { min ,  }

           { min , max }

    characterclass = characterrange

                     characterrange characterclass

    characterrange = begincharacter

                     begincharacter - endcharacter

    begincharacter = character

    endcharacter = character

    character =

                anycharacterexceptmetacharacters

                \ anycharacterexceptspecialcharacters

    metacharacter = ?

                    * {=0 or more, greedy}

                    *? {=0 or more, non-greedy}

                    + {=1 or more, greedy}

                    +? {=1 or more, non-greedy}

                    ^ {=begin of line character}

                    $ {=end of line character}

                    $` {=the characters to the left of the match}

                    $' {=the characters to the right of the match}

                    $& {=the characters that are matched}

                    \t {=tab character}

                    \n {=newline character}

                    \r {=carriage return character}

                    \f {=form feed character}

                    \cX {=control character CTRL-X}

                    \N {=the characters in Nth tag (if on match side)}

                    $N{=the characters in Nth tag (if not on match side)}

                    \NNN {=octal code for character NNN}

                    \b {=match a 'word' boundary}

                    \B {=match not a 'word' boundary}

                    \d {=a digit, [0-9]}

                    \D {=not a digit, [^0-9]}

                    \s {=whitespace, [ \t\n\r\f]}

                    \S {=not a whitespace, [^ \t\n\r\f]}

                    \w {='word' character, [a-zA-Z0-9_]}

                    \W {=not a 'word' character, [^a-zA-Z0-9_]}

                    \Q {=put a quote (de-meta) on characters, until \E}

                    \U {=change characters to uppercase, until \E}

                    \L {=change characters to uppercase, until \E}

    min = integer

    max = integer

    integer = digit

              digit integer

    anycharacter = ! " # $ % & ' ( ) * + , - . / :
                   ; < = > ? @ [ \ ] ^ _ ` { | } ~
                   0 1 2 3 4 5 6 7 8 9
                   A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
                   a b c d e f g h i j k l m n o p q r s t u v w x y z
     */

}
