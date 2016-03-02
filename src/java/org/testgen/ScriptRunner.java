package org.testgen;

import groovy.lang.Binding;

import org.apache.log4j.Logger;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import org.testgen.loggers.Log;; 

public class ScriptRunner
{
    private static final Logger log = Log.getExecutionLogger();
    
    @Test
    public void test()
    {
        try
        {
            Binding binding = new Binding();
            binding.setVariable("success", Boolean.FALSE);
        }
        catch(Exception ex)
        {
            throw new Error("Internal error", ex);
        }
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
