def jnlpUrl = "${properties['gen.jnlp.url']}"
def libDir  = "${properties['gen.lib.dir']}"

println "jnlpUrl = ${jnlpUrl}"
println "libDir  = ${libDir}"

def jnlpPath        = libDir + "/test-app.jnlp"
def runPropertyPath = libDir + "/run.properties"

getFileContent(jnlpUrl, jnlpPath)

def jnlpConfig = new XmlParser().parse(new File(jnlpPath))
assert jnlpConfig.name() == 'jnlp'

// Get codebase
def codebase = jnlpConfig.'@codebase'
println "codebase = ${codebase}"

// Save properties to file
print "Saving run.properties ".padRight(55)

new File(runPropertyPath).withWriter
{ 
    out ->
    
    jnlpConfig.resources.property.each { out.println "${it.'@name'}=${it.'@value'}" }
}

println "... OK"

// Getting jar files
jnlpConfig.resources.jar.each
{
    def jar     = it.'@href'
    def version = it.'@version'
    
    def jarUrl  = codebase + "/" + it.'@href'
    
    if (version)
    {
        def  idx = jar.lastIndexOf(".jar");
        
        if (idx != -1)
        {
            jar = new StringBuffer(jar).insert(idx, "-" + version).toString();
        }

        jarUrl = jarUrl + "?version-id=" + version;
    }
    
    getFileContent(jarUrl, libDir + "/" + jar)
}

println "Finished successfully"

def getFileContent(url, path)
{
    def file = new File(path).newOutputStream()  
    
    def arr = url.split("/")
    print "Downloading ${arr[arr.length - 1]} ".padRight(55)
    
    file << new URL(url).openStream()  
    file.close()
    
    println "... OK" 
}
