package org.testgen.loggers;

import org.apache.log4j.Logger;

public final class Log
{
    private Log()
    {
    }
    
    public static Logger getExecutionLogger()
    {
        return Logger.getLogger("EXECUTION");
    }
}
