package net.sf.jml;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Roger Chen
 */
public class JmlAllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(JmlAllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.sf.jml");
        //$JUnit-BEGIN$
        suite.addTestSuite(EmailTest.class);
        //$JUnit-END$
        return suite;
    }
}
