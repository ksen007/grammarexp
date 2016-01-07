package edu.berkeley.grammarexp.expressions;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/6/16
 * Time: 9:51 AM
 */
public class GExpCompilerTest {

    @Test
    public void test1() throws Exception {
        String expected = "(%GE (%GE (%GE (%GE a %)(%GE (%GE b %)* %) %)(%GE a %) %)|(%GE a %) %)";
        String actual = (new GExpCompiler("ab*a|a")).parse("ab*a|a");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() throws Exception {
        //String expected = "(%GE (%GE (%GE (%GE a %)(%GE (%GE b %)* %) %)(%GE a %) %)|(%GE a %) %)";
        GExpCompiler gexp = (new GExpCompiler("(a|b)*aba(a|b)*"));
        Assert.assertEquals(true, gexp.match("aabab"));
        Assert.assertEquals(false, gexp.match("aabbab"));
        Assert.assertEquals(false, gexp.match("a"));
        Assert.assertEquals(false, gexp.match("bbbbbbba"));
        Assert.assertEquals(true, gexp.match("aba"));
        Assert.assertEquals(true, gexp.match("ababa"));

//        Assert.assertEquals(expected, actual);
    }
}