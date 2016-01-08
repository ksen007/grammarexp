package edu.berkeley.grammarexp.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/8/15
 * Time: 10:12 AM
 */
public class CharStreamScanner extends Scanner {
    protected Reader stream;


    public CharStreamScanner(Grammar g) {
        super(g);
    }

    public void setStream(Reader stream) {
        this.stream = stream;
    }

    @Override
    public int nextToken() throws IOException {
        int inp = stream.read();
        if (inp == -1) {
            tokenID = g.endToken;
        } else {
            tokenID = g.getTerminalID((char)inp);
        }
        tokenValue = (char)inp;
        return tokenID;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
