def getEnvVariablesWithPrefix(prefix){
    env = System.getenv()
    return env.keySet().findAll{it.startsWith(prefix)}.collect {env[it]}
}

print(getEnvVariablesWithPrefix("CYPRES_JS_"))
