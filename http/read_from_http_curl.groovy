final String gitFileDescription = sh(script: "curl -s $url", returnStdout: true).trim()

// json parsing
def gitFileDescriptionJson = new groovy.json.JsonSlurper().parseText(gitFileDescription)
gitFileDescriptionJson.download_url

