package edu.berkeley.grammarexp.expressions;

import edu.berkeley.grammarexp.parser.Grammar;
import edu.berkeley.grammarexp.parser.Scanner;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/7/16
 * Time: 7:35 PM
 */

class TrieNode {
    int ID;
    String s;
    boolean isSet;
    Map<Character, TrieNode> children;

    public TrieNode() {
        isSet = false;
        children = null;
    }

    public TrieNode get(char c) {
        if (children == null) return null;
        else return children.get(c);
    }

    public TrieNode getOrCreate(char c) {
        TrieNode ret;
        if (children == null) {
            children = new TreeMap<Character, TrieNode>();
        }
        if ((ret = children.get(c)) == null) {
            ret = new TrieNode();
            children.put(c, ret);
        }
        return ret;
    }
}

public class StreamTokenizer extends Scanner {
    TrieNode trie;
    Reader stream;

    public StreamTokenizer(Grammar g) {
        super(g);
        trie =  new TrieNode();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    public void setStream(Reader stream) {
        this.stream = stream;
    }

    public void addString(int ID, String token) {
        int len = token.length();
        TrieNode current = trie;

        for (int i = 0; i < len; i++){
            char c = token.charAt(i);
            current = current.getOrCreate(c);
        }
        current.ID = ID;
        current.s = token;
        current.isSet = true;
    }


    public void addString(int ID) {
        String s = (String)g.getSymbolFromID(ID);
        addString(ID, s);
    }

    @Override
    public int nextToken() throws IOException {
        int inp = stream.read();
        if (inp == -1) {
            tokenValue = (char) inp;
            return (tokenID = g.endToken);
        }
        stream.mark(10);
        int firstInp = inp;
        TrieNode prev = null;
        TrieNode ret = trie.get((char)inp);
        while(ret != null) {
            inp = stream.read();
            prev = ret;
            ret = trie.get((char)inp);
        }
        if (prev != null && prev.isSet) {
            this.tokenID = prev.ID;
            this.tokenValue = prev.s;
            return prev.ID;
        } else {
            stream.reset();
            tokenValue = (char) firstInp;
            tokenID =  g.getTerminalID((char) firstInp);
            return tokenID;
        }
    }

}
