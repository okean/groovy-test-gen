import au.com.bytecode.opencsv.CSVReader;

println "\tGENERATE TESTS ---\r\n"

def generatedTotal = 0

def dataDir    = "D:/Projects/groovy-test-gen/src/generator-data"
def destDir    = "D:/Projects/groovy-test-gen/target/generated-resources/groovy/tests"

// Resources paths
//def dataDir    = "${properties['gen.data.directory']}"
//def destDir    = "${properties['gen.dest.directory']}"
def configPath = "${dataDir}/config.xml"

// Read configuration file
def config = new XmlParser().parse(new File(configPath))

assert config.name() == 'config'
config.children().each { assert it.name() == 'item' }

config.item.findAll 
{
    def testTemplateFilePath    = dataDir + "/" + it.'@test-template-file'
    def dataFilePath            = dataDir + "/" + it.'@data-file'
    def mappingFilePath         = dataDir + "/" + it.'@mapping-file'

    def testName                = it.'@test-name'
    def desc                    = it.'@desc'

    println "\tGenerating tests for '${desc}'"
    
    def testTemplate            = new File(testTemplateFilePath).getText()
    def mapping                 = new XmlParser().parse(new File(mappingFilePath))
    
    def scriptCode              = new StringBuffer()
    
    def (header, rows)          = loadDataFromCsvFile(dataFilePath)
    
    rows.eachWithIndex 
    { 
        row, rIdx ->
        
        // Find out the previous and the next case name (multiline case feature)
        def thisCase = row[0]
        def prevCase = rIdx > 0 && rows[rIdx - 1].size() > 0 ? rows[rIdx - 1][0] : null
        def nextCase = rIdx < rows.size() - 1 && rows[rIdx + 1].size() > 0 ? rows[rIdx + 1][0] : null

        // Clear code for a new case
        if (thisCase != prevCase)
            scriptCode.delete(0, scriptCode.size())

        header.eachWithIndex 
        { 
            alias, hIdx ->
            
            def value = row[hIdx]

            // Skip empty values
            if (value == null || value.length() == 0)
                return

            // use '/' as a delimiter of values
            def values = value.split("/")

            // Get mapping item by alias
            def mapItem = mapping.item.find
            { 
                it.'@alias' == alias 
            }

            if (mapItem == null)
            {
                if (alias != 'case')
                    throw new Exception("'${alias}' mapping is not found")
                return
            }

            def snippet = mapItem."@snippet" // e.g., assert x != '$v'
            
            def valuesCount = values.length
            
            // substitute variables $v1,$v2,..,$vn-1
            valuesCount.times 
            { 
                n ->
                
                def key = "\$v${n + 1}"
                def v   = values[n]
                
                if (snippet != null)
                {
                    snippet = snippet.replace(key, v)
                }
            }

            // Modify the code
            if (snippet != null) 
            {
                snippet = snippet.replace('$blank', '')
                scriptCode.append("${snippet}\n")
            }
        }

        if (thisCase != nextCase)
        {
            // Build the test body
            def testCode = testTemplate.replace('$code', scriptCode)

            // Prepare the test file name
            // The first column of data must be a case name
            def testNameTmp = testName.replace('$case', row[0])

            // Create recursive directories
            testNameTmp = testNameTmp.replace("\\", "/")
            
            if (testNameTmp.contains("/"))
                new File(destDir +"/" + testNameTmp.substring(0, testNameTmp.lastIndexOf('/'))).mkdirs()

            // Write the test file
            def testPath = "${destDir}/${testNameTmp}"
            new File(testPath).write(testCode)
            println "+ ${testNameTmp}"
            
            generatedTotal++
        }
    }
}

println "\r\n -----------------------------------------------------------------"
println " Generated total of ${generatedTotal} tests"
println " -----------------------------------------------------------------\r\n"

def loadDataFromCsvFile(dataFilePath)
{
    def csvReader = new CSVReader(new FileReader(dataFilePath));
    def header = null
    def rows = []

    csvReader.readAll().each
    {
        row ->
        
        if (row.size() == 0 || row.first().allWhitespace)
            return

        if (!header)
        {
            if (row.first() == 'case')
            {
                header = row
            }
            
            return
        }
        
        rows.add(row)
    }
    
    return [header, rows]
}
