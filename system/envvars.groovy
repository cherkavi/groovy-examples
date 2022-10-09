def pathValue = System.getenv("IS_DEBUG").toLowerCase()=="true";
println("IS_DEBUG="+pathValue)
if (!pathValue){
    println("without debug")
}else{
    println("WITH DEBUG")
}
