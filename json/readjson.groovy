/*
{
  "name": "visions.md",
  "path": "visions.md",
  "download_url": "usb.net/raw/swh/documentation/master/visions.md?token=A6F3CBZT4Q"
}  
*/
textFromFile = new File("1.json").text
result = new groovy.json.JsonSlurper().parseText(textFromFile)
println(result.download_url)
