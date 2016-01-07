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
        String actual = (new GExpCompiler()).parse("ab*a|a");
        Assert.assertEquals(expected, actual);
    }
}