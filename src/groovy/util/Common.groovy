import groovy.lang.Script;

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
    
    static def runScript(Script currentScript, String scriptPath, String[] arguments)
    {
        def currentScriptDir = currentScript.binding.getProperty("scriptDir")
        
        if (scriptPath[0] != '/')
            scriptPath = '/' + scriptPath
        
        if (!currentScriptDir.isEmpty())
            scriptPath = '/' + currentScriptDir + scriptPath

        log "Running script: ${scriptPath} ${arguments}"
        
        def scriptFile = new File(Common.class.getResource(scriptPath).toURI())
        currentScript.run(scriptFile, arguments)
    }
}
