println "GENERATE TESTS"

// Resources paths
def dataDir    = "${properties['gen.data.directory']}"
def destDir    = "${properties['gen.dest.directory']}"
def configPath = "${dataDir}/config.xml"

// Read configuration file
def config = new XmlParser().parse(new File(configPath))

assert config.name() == 'config'
config.children().each { assert it.name() == 'item' }
