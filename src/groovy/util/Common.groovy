import org.apache.log4j.Logger
import org.testng.Assert

import org.testgen.loggers.Log

class Common
{
    static def final Logger log = Log.getExecutionLogger()
    
    static def log(message)
    {
        log.info(message)
    }
}
