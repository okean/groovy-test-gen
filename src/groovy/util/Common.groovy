import groovy.lang.Script;
import groovy.xml.XmlUtil

import org.apache.log4j.Logger
import org.testng.Assert
import org.testgen.loggers.Log

class Common
{
    private static def final Logger log = Log.getExecutionLogger()
    
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
    
    static def saveValue(String key, String value)
    {
        log "saving key = '${key}', value = '${value}'"
        
        def (store, data) = readStoredData()
        
        def item = data.item.find { it."@key" == key }
        
        item = item ?: data.appendNode("item", ["key" : key])
        
        item."@value" = value
        item."@date" = new Date().format('dd-MM-yyyy HH:mm')
        
        store.withOutputStream { XmlUtil.serialize(data, it) }
    }
    
    static def loadValue(String key)
    {
        log "loading key = '${key}'"
        
        def (store, data) = readStoredData()
        
        def item = data.item.find { it."@key" == key }
        def value = !item ?: item."@value"
        
        return value
    }
    
    private static def readStoredData()
    {
        def storePath = "${System.getProperty('log.path')}/store.xml"
        
        def store = new File(storePath)
        def data = new XmlParser().parseText(store.exists() ? store.getText() : "<data/>")
        
        return [store, data]
    }
}
