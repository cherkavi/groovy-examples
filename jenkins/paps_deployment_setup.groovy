package com.ubs.productconfig.jenkins.seed
/*
 * Jenkins seed job for all continuous integration jobs (deployable war files, jars and QA parts) 
 * of VKD team product configuration (TMCP).
 */

gitHostUrl = "ssh://git@webgit.wireswiss.de:7999/"

gitBaseUrl = gitHostUrl + 'pportal/'

mailerAdresses = [:]
mailerAdresses["cable"] = "steffen_@ubs.com"
mailerAdresses["paps-opm-deployment"] = "steffen_@ubs.com"
mailerAdresses["paps-mobile-config-deployment"] = "steffen_@ubs.com, peter_@ubs.com"
mailerAdresses["fixed"] = "steffe@ubs.com"
mailerAdresses["scriptgen"] = "thomas_@ubs.com"

createDeploymentFolder()
createOpmDeploymentJob("opm.git", "paps-opm-deployment")
createMobileConfigDeploymentJob("mobile-config.git", "paps-mobile-config-deployment")

def createDeploymentFolder() {
	folder("paps-deployment")
}

def createOpmDeploymentJob(gitRepository, pipelineName) {
    job("paps-deployment/" + pipelineName) {
        scm {
            git {
                remote {
                    url(gitBaseUrl + gitRepository)
                    credentials("git_jenkins")
                    branch('${srcBranch}')
                }
            }
        }
        parameters {
            booleanParam('reInstallDB', false, 'Re-Install Database')
            booleanParam('updateDB', false, 'Update Database')
            gitParam('srcBranch') {
                description('Source Branch')
                type('BRANCH')
            }
            choiceParam('environment', [
                    'DEV2',
                    'DEV3',
                    'TEST4',
                    'SIT'
            ], "Target Environment")
        }
        steps {
            configureDBReInstall(delegate, "DEV2", "-U package -Dmaven.test.skip -Preinstall-db -Ddb.stage=int -Ddb.host=vspn050.wireswiss.de -Ddb.port=1523 -Ddb.service=PMDR -Ddb.user=OPM -Ddb.pass=1")
            configureDBReInstall(delegate, "DEV3", "-U package -Dmaven.test.skip -Preinstall-db -Ddb.stage=int -Ddb.host=vspn050.wireswiss.de -Ddb.port=1523 -Ddb.service=PMDR -Ddb.user=OPM_PROD -Ddb.pass=1")
            configureDBReInstall(delegate,  "TEST4", "-U package -Dmaven.test.skip -Preinstall-db -Ddb.stage=int -Ddb.host=pmdrt4-pub1.wireswiss.de -Ddb.port=1525 -Ddb.service=PMDRT4 -Ddb.user=OPM -Ddb.pass=opmTEST4_application")
            configureDBUpdate(delegate, "DEV2", "-U package -Dmaven.test.skip -Pupdate-db -Ddb.stage=int -Ddb.host=vspn050.wireswiss.de -Ddb.port=1523 -Ddb.service=PMDR -Ddb.user=OPM -Ddb.pass=1")
            configureDBUpdate(delegate, "DEV3", "-U package -Dmaven.test.skip -Pupdate-db -Ddb.stage=int -Ddb.host=vspn050.wireswiss.de -Ddb.port=1523 -Ddb.service=PMDR -Ddb.user=OPM_PROD -Ddb.pass=1")
            configureDBUpdate(delegate, "TEST4", "-U package -Dmaven.test.skip -Pupdate-db,update-db-kias -Ddb.host=pmdrt4-pub1.wireswiss.de -Ddb.port=1525 -Ddb.service=PMDRT4 -Ddb.user=OPM -Ddb.pass=opmTEST4_application -Dkias.db.user=KIAS -Dkias.db.password=kiasTEST4_application")
            configureDeployment(delegate, "DEV2", "-U clean install -Dmaven.test.skip -Predeploy-wls -Dwls.host=vldn080.wireswiss.de -Dremote=true -Dupload=true -Dadmin.server=pportal_group -Djpa.datasource.schema=OPM -Dliquibase.defaultSchema=OPM -Dspring.profiles.active=integration -Ddb.stage=int")
            configureDeployment(delegate, "DEV3", "-U clean install -Dmaven.test.skip -Predeploy-wls -Dwls.host=vldn037.wireswiss.de -Dremote=true -Dupload=true -Dadmin.server=pportal_group -Djpa.datasource.schema=OPM_PROD -Dliquibase.defaultSchema=OPM_PROD -Dspring.profiles.active=oracle -Ddb.stage=int")
            configureDeployment(delegate, "TEST4", "-U clean install -Dmaven.test.skip -Predeploy-wls -Dwls.host=vltn953.wireswiss.de -Dremote=true -Dupload=true -Dadmin.server=pportal_group -Djpa.datasource.schema=OPM_PROD -Dliquibase.defaultSchema=OPM_PROD -Dspring.profiles.active=oracle -Ddb.stage=int")
            configureDeployment(delegate, "SIT", "-U clean install -Dmaven.test.skip -Pdeploy-tomcat -Dtomcat.baseUrl=http://deossaavr.dc-ratingen.de:8080/ -Dtomcat.username=admin -Dtomcat.password=paps123")
            configureDeployment(delegate, "SIT", "-U clean install -Dmaven.test.skip -Pdeploy-tomcat -Dtomcat.baseUrl=http://deossabvr.dc-ratingen.de:8080/ -Dtomcat.username=admin -Dtomcat.password=paps123")
        }
        publishers {
            mailer(mailerAdresses[pipelineName], true, true)
        }
    }
}

def createMobileConfigDeploymentJob(gitRepository, pipelineName) {
	job("paps-deployment/" + pipelineName) {
		scm {
			git {
				remote {
					url(gitBaseUrl + gitRepository)
					credentials("git_jenkins")
					branch('${srcBranch}')
				}
			}
		}
		parameters {
			gitParam('srcBranch') {
				description('Source Branch')
				type('BRANCH')
			}
			choiceParam('environment', [
				'DEV1',
				'TEST5',
			], "Target Environment")
		}
		steps {
			configureDeployment(delegate, "DEV1", "clean install -Dmaven.test.skip -Pprepare-stage,deploy-wls -Dwls.host=vldn657.wireswiss.de -Dremote=true -Dupload=true -Dadmin.server=pportal_group -Djpa.datasource.schema=PMDR -Dliquibase.defaultSchema=PMDR -Dspring.profiles.active=integration")
			configureDeployment(delegate, "TEST5", "clean install -Dmaven.test.skip -Pprepare-stage,deploy-wls -Dwls.host=vltn670.wireswiss.de -Dremote=true -Dupload=true -Dadmin.server=pportal_group -Djpa.datasource.schema=PMDR -Dliquibase.defaultSchema=PMDR -Dspring.profiles.active=integration")
		}
		publishers {
			mailer(mailerAdresses[pipelineName], true, true)
		}
	}
}

// The "and" operator of conditionalSteps is broken in Jenkins Job DSL plugin V1.67 :-( - Therefore this weird interlacing of steps.

def configureDBUpdate(context, argEnvironment, mvnGoals) {
	context.with {
		conditionalSteps {
			condition {stringsMatch('${environment}', argEnvironment, false)}
			steps {
				conditionalSteps {
					condition {booleanCondition('${updateDB}')}
					steps {mvn(delegate, mvnGoals)} 
				}
			}
		}
	}
}

def configureDBReInstall(context, argEnvironment, mvnGoals) {
	context.with {
		conditionalSteps {
			condition {stringsMatch('${environment}', argEnvironment, false)}
			steps {
				conditionalSteps {
					condition {booleanCondition('${reInstallDB}')}
					steps {mvn(delegate, mvnGoals)}
				}
			}
		}
	}
}

def configureDeployment(context, argEnvironment, mvnGoals) {
	context.with {
		conditionalSteps {
			condition {
				stringsMatch('${environment}', argEnvironment, false)
			}
			steps { mvn(delegate, mvnGoals) }
		}
	}
}


def mvn(context, mvnGoals) {
	context.with {
		maven {
			mavenInstallation('mvn-325')
			providedSettings('paps-maven-settings')
			goals(mvnGoals)
		}
	}
}

