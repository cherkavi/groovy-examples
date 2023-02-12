package com.ubs.productconfig.jenkins.backup
/*
 * Jenkins seed job for all continuous integration jobs (deployable war files, jars and QA parts) 
 * of VKD team product configuration (TMCP).
 */

gitBaseUrl = 'https://webgit.wireswiss.de/scm/pportal/'
bitbucketBaseUrl = 'https://webgit.wireswiss.de/projects/PPORTAL/repos'

mavenVersion = 'mvn-325'

mailerAdresses = [:]
mailerAdresses["commons"] = "DL-DE-KD-Produktkonfig_Entwickler@internal.ubs.com"
mailerAdresses["base"] = "DL-DE-KD-Produktkonfig_Entwickler@internal.ubs.com"
mailerAdresses["cable"] = "produktkonfigentwicklercable@ubs.com"
mailerAdresses["mobile"] = "produktkonfigentwicklermobile@ubs.com"
mailerAdresses["fixed"] = "produktkonfigentwicklerfixed@ubs.com"
mailerAdresses["scriptgen"] = "thomas@ubs.com"

/* 
 * Application jobs to create for continuous integration
 * createApplicationJob(repo_name, branch_name, upstream_project, mailer_adresses, enabled)
 */
createApplicationJob('commons', '*/develop', null, 'commons', false) //should be true
createApplicationJob('commons', '*/master', null, 'commons', false) //should be true
createApplicationJob('commons', '*/feature/ci-pmdrt1', null, 'commons', false) //should be true
createApplicationJob('common-wicket6', '*/develop', 'commons---develop', 'commons', false) //should be true
createApplicationJob('common-wicket6', '*/master', null, 'commons', false) //should be false
createApplicationJob('base', '*/master', null, 'base', false) //should be true
createApplicationJob('fixnet', '*/develop', null, 'fixed', false) //should be true
createApplicationJob('fixnet', '*/master', null, 'fixed', false) //should be true
createApplicationJob('fixnet', '*/hotfix/*', null, 'fixed', false) //should be false
createApplicationJob('fixnet', '*/feature/*', null, 'fixed', false) //should be true
createApplicationJob('fixnet', '*/release/*', null, 'fixed', false) //should be true
createApplicationJob('fixnet', '*/staging', null, 'fixed', false) //should be true
createApplicationJob('opm', '*/master', 'base---master', 'mobile', false) //should be true
createApplicationJob('mobile-config', '*/develop', 'base---master', 'mobile', false) //should be true
createApplicationJob('mobile-config', '*/master', null, 'mobile', false) //should be true
createApplicationJob('mobile-config', '*/release/*', null, 'mobile', false) //should be true
createApplicationJob('paps-cable', '*/develop', 'common-wicket6---develop', 'cable', false) //should be true
createApplicationJob('paps-cable', '*/master', 'common-wicket6---master', 'cable', false) //should be false
createApplicationJob('paps-cable', '*/feature/*', null, 'cable', false) //should be false
createApplicationJob('paps-cable', '*/release/*', null, 'cable', false) //should be false
createApplicationJob('pcscriptgeneration', '*/develop', null, 'scriptgen', false) //should be true

/* 
 * Quality assurance jobs to create
 * createQualityAssuranceJob(repo_name, branch_name, upstream_project, mailer_adresses, enabled)
 */
createQualityAssuranceJob('mobile-config', '*/develop', null, 'mobile', false) //should be true
createQualityAssuranceJob('opm', '*/master', null, 'mobile', false) //should be true
createQualityAssuranceJob('fixnet', '*/develop', null, 'fixed', false) //should be true

/* 
 * Remote jobs (e.g. for selenium integration tests) to create
 * createQualityAssuranceJob(repo_name, branch_name, label_name, mailer_adresses, enabled)
 */
createRemoteJob('mobile-config', '*/develop', 'Steffens_Laptop', 'mobile', false) //should be true

createListView('commons')
createListView('mobile-config')
createListView('paps-cable')
createListView('opm')

def createApplicationJob(repo_name, branch_name, upstream_project, mailer_adresses, enabled) {
    job("${repo_name} ${branch_name}".replaceAll(~/\W+$/,'').replaceAll(~/\W+/,'-')) {
        logRotator(-1, 10, -1, -1)

        label('master')

		if (!enabled) {
			disabled()
		}

        scm {
		  git {
			remote {
			  credentials 'git_jenkins'
			  url("${gitBaseUrl}/${repo_name}.git")
			}
			branch branch_name
			browser {
				stash("${bitbucketBaseUrl}/${repo_name}/browse")
			}
		  }
		}
		
		//jdk('Java 7')

        triggers {
            scm('H/5 * * * *')
			if (upstream_project != null) {
				upstream(upstream_project,"SUCCESS")
			}
        }

		switch (repo_name) {
			case 'fixnet':
				steps {
					maven {
						mavenInstallation mavenVersion
						goals 'clean package flyway:clean flyway:migrate -pl persistence  -DskipTests=true -P addtestdata'
					}
				}
				steps {
					maven {
						mavenInstallation mavenVersion
						goals 'flyway:clean flyway:migrate -pl persistence'
					}
				}
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U -X clean deploy -Dbuild.number="${BUILD_NUMBER}, rev ${GIT_COMMIT} [${GIT_BRANCH}]"'
					}
				}				
				break
			case 'opm':
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U clean deploy -Dmaven.javadoc.skip=true -Dbuild.number=${GIT_BRANCH}-#${BUILD_NUMBER}'
					}
				}
				break
			case 'mobile-config':
				steps {
					maven {
						mavenInstallation mavenVersion
						if (branch_name.contains('master')) {
							goals '-U clean deploy -Dmaven.javadoc.skip=true -Dbuild.number=${BUILD_NUMBER} -Ppmd'
						} else {
							goals '-U clean deploy -Dmaven.javadoc.skip=true -Dbuild.number=${GIT_BRANCH}-#${BUILD_NUMBER} -Ppmd -Pprepare-stage'
						}
					}
				}
			case 'paps-cable':
				steps {
					maven {
						mavenInstallation mavenVersion
						if (branch_name.contains('feature')) {
							goals '-U clean deploy -Dbuild.number=feature.${BUILD_NUMBER}.${GIT_COMMIT} -DskipTests=true'
						} else {
							goals '-U clean deploy -Dbuild.number=${BUILD_NUMBER}.${GIT_COMMIT}'
						}
					}
				}
				break	
			default:
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U clean deploy'
					}
				}
				break
		}
        
		
		publishers {
			//if (repo_name == 'fixnet' || repo_name == 'opm' || repo_name == 'mobile-config') {
			//	archiveArtifacts '*gui/target/*.war'
			//} else {
			//	archiveArtifacts '*/target/*.jar'
			//}
			//
			
			if (repo_name == 'mobile-config' || repo_name == 'opm') {
				pmd('**/*.pmd')
			}
			archiveJunit('*/target/surefire-reports/*xml')
			chucknorris()
			mailer(mailerAdresses[mailer_adresses], true, true)
		}
    }
}

def createQualityAssuranceJob(repo_name, branch_name, upstream_project, mailer_adresses, enabled) {
    job("${repo_name} ${branch_name} Quality Assurance".replaceAll(~/\W+$/,'').replaceAll(~/\W+/,'-')) {
        logRotator(-1, 10, -1, -1)

        label('master')
		
		if (!enabled) {
			disabled()
		}

        scm {
		  git {
			remote {
			  credentials 'git_jenkins'
			  url("${gitBaseUrl}/${repo_name}.git")
			}
			branch branch_name
			browser {
				stash("${bitbucketBaseUrl}/${repo_name}/browse")
			}
		  }
		}
		
		//jdk('Java 7')

        triggers {
            cron('@midnight')
			if (upstream_project != null) {
				upstream(upstream_project,"SUCCESS")
			}
        }

		switch (repo_name) {
			case 'fixnet':
				steps {
					maven {
						mavenInstallation mavenVersion
						goals 'clean package flyway:clean flyway:migrate -pl persistence -DskipTests=true'
					}
				}
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U clean deploy -Dbuild.number="${BUILD_NUMBER}, rev ${GIT_COMMIT} [${GIT_BRANCH}]"'
					}
				}				
				break
			case 'opm':
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U clean deploy -Dmaven.javadoc.skip=true -Dbuild.number=${GIT_BRANCH}-#${BUILD_NUMBER} -Pstatic_weave,pmd'
					}
				}
				break
			case 'mobile-config':
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U clean deploy -Dbuild.number=${BUILD_NUMBER} -Pstatic_weave,pmd'
					}
				}
				break				
			default:
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U clean deploy'
					}
				}
				break
		}
		
		configure {
			it / 'builders' / 'hudson.plugins.sonar.SonarRunnerBuilder'
		}
        
		publishers {
			chucknorris()
			mailer(mailerAdresses[mailer_adresses], true, true)
		}
	}
}

def createRemoteJob(repo_name, branch_name, label_name, mailer_adresses, enabled) {
    job("${repo_name} ${branch_name} ${label_name}".replaceAll(~/\W+$/,'').replaceAll(~/\W+/,'-')) {
        logRotator(-1, 10, -1, -1)

        label label_name
		
		if (!enabled) {
			disabled()
		}

        scm {
		  git {
			remote {
			  credentials 'git_jenkins'
			  url("${gitBaseUrl}/${repo_name}.git")
			}
			branch branch_name
			browser {
				stash("${bitbucketBaseUrl}/${repo_name}/browse")
			}
		  }
		}
		
		triggers {
            cron('@weekly')
        }
		
		switch (repo_name) {
			case 'mobile-config':
				steps {
					maven {
						mavenInstallation mavenVersion
						goals 'clean install -DskipTests -DskipITs=false -Pintegration-tests,dev'
					}
				}	
				break
			default:
				steps {
					maven {
						mavenInstallation mavenVersion
						goals '-U clean deploy'
					}
				}
				break
		}
		
		publishers {
			archiveJunit('*gui/target/failsafe-reports/*.xml')
			chucknorris()
			mailer(mailerAdresses[mailer_adresses], true, true)
		}
	}
}

def createListView(pattern) {
	listView('TMCP ' + pattern.toUpperCase()) {
		description('All TMCP ' + pattern.toUpperCase() + ' projects')
		jobs {
			regex('/' + pattern + '.*/')
		}
		columns {
			status()
			weather()
			name()
			lastSuccess()
			lastFailure()
			lastDuration()
			buildButton()
		}
	}
}
