package org.testgen;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

public class ScriptRunner
{
    @Test
    public void test()
    {
    }

    public static void main(String[] args)
    {
        TestNG testng = new TestNG();
        
        testng.setTestClasses(new Class[] { ScriptRunner.class });
        testng.addListener(new TestListenerAdapter());
        testng.run();
        
        System.exit(testng.hasFailure() ? 1 : 0);
    }
}
