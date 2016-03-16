package org.testgen;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.apache.log4j.Logger;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testgen.loggers.Log;

import static org.testng.Assert.fail;

public class ScriptRunner
{
    private static final Logger log = Log.getExecutionLogger();
    
    @Test
    public void test()
    {
        try
        {
            Binding binding = new Binding();
            binding.setVariable("succeed", Boolean.FALSE);
            binding.setVariable("now", new Date());
            
            Reader scriptReader = new InputStreamReader(new SequenceInputStream(
                    getTestFileStream(), getTailFileStream()));
            
            String scriptName = getTestFileName().replaceAll("\\\\", "/");
            String scriptDir = "";

            int idx = scriptName.lastIndexOf("/");

            if (idx != -1)
            {
                scriptDir = scriptName.substring(0, idx);
                scriptName = scriptName.substring(idx + 1, scriptName.length());
            }
            
            binding.setVariable("scriptDir", scriptDir);
            
            scriptName = scriptName.replaceAll(".", "_").replaceAll("-", "_");
                        
            GroovyShell sh = new GroovyShell(binding);

            try
            {
                log.info("Start script execution: " + getTestFileName());
                
                sh.evaluate(scriptReader, scriptName);
              
                boolean succeed = (Boolean) binding.getVariable("succeed");

                if (succeed)
                {
                    log.info("Script executed successfully: " + getTestFileName());
                }
                else
                {
                    log.info("Script did not shutdown cleanly: " + getTestFileName());
                    
                    fail("Script did not shutdown cleanly");
                }
            }
            catch (Exception ex)
            {
                log.info("Script execution aborted due to exception: " + exceptionBriefDescription(ex));
                log.info(scriptExceptionDescription(ex, scriptName));
                
                throw new AssertionError(ex);
            }
            catch (AssertionError ex)
            {
                log.info("FAIL: " + ex.getMessage());
                log.info(scriptExceptionDescription(ex, scriptName));
                
                throw new AssertionError(ex);
            }
            catch (Throwable ex)
            {
                log.info("Script execution aborted due to unexpected throwable: " + exceptionBriefDescription(ex));
                log.info(scriptExceptionDescription(ex, scriptName));
                
                throw new AssertionError(ex);
            }
        }
        catch(Exception ex)
        {
            throw new Error("Internal error", ex);
        }
    }

    private static String getTestFileName()
    {
        String testFileName = System.getProperty("test-gen.test.name");
        
        if (testFileName == null)
        {
            throw new IllegalArgumentException("test-gen.test.name is undefined");
        }
            
        return testFileName;
    }
    
    private static String exceptionBriefDescription(Throwable ex)
    {
        String descr = ex.getClass().getSimpleName();
        
        if (ex.getMessage() != null && ex.getMessage().length() > 0)
        {
            descr = descr + " \"" + ex.getMessage() + "\"";
        }
        return descr;
    }

    private static String scriptExceptionDescription(Throwable e, String scriptName)
    {
        Map<String, String> groovyLines = new TreeMap<String, String>();
        
        for (StackTraceElement item : e.getStackTrace())
        {
            final String fileName = item.getFileName();
            
            if (fileName != null && fileName.endsWith(".groovy"))
            {
                final String prevValue = groovyLines.get(fileName);
                final Integer n = item.getLineNumber();
                
                groovyLines.put(fileName, (prevValue != null ? prevValue + ", " : "") + n);
            }
        }

        final List<String> groovyLinesOrdered = new ArrayList<String>(groovyLines.keySet());
        Collections.reverse(groovyLinesOrdered);

        StringBuilder sb = new StringBuilder();
        
        for (String fileName: groovyLinesOrdered)
        {
            sb.append(sb.length() > 0 ? "," : "")
              .append(fileName)
              .append("(" + groovyLines.get(fileName) + ")");
        }

        return sb.toString() + ": " + e.getMessage();
    }
    
    private InputStream getTailFileStream()
    {
        return getClass().getResourceAsStream("/tail.groovy");
    }

    private InputStream getTestFileStream() throws FileNotFoundException
    {
            return getClass().getResourceAsStream("/" + getTestFileName());
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
