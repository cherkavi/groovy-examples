package com.ubs.productconfig.jenkins.seed
/*
 * Jenkins seed job for all continuous integration jobs (deployable war files, jars and QA parts) 
 * of VKD team product configuration (TMCP).
 */

gitHostUrl = "ssh://git@webgit.wireswiss.de:7999/"

gitBaseUrl = gitHostUrl + 'pportal/'

createPipelineFolder();
createMultibranchPipeline("commons.git", "paps-commons", " master ")
createMultibranchPipeline("base.git", "paps-base", "develop")
createMultibranchPipeline("opm.git", "paps-opm", "develop master release* hotfix*")
createMultibranchPipeline("mobile-config.git", "paps-mobile-config", "develop master release* hotfix*")
createMultibranchPipeline("fixnet.git", "paps-fixnet", "develop")
createMultibranchPipeline("paps-cable.git", "paps-cable", "develop")

def createPipelineFolder() {
	folder("paps-pipelines")
}

def createMultibranchPipeline(gitRepository, pipelineName, branchPattern) {
	multibranchPipelineJob("paps-pipelines/" + pipelineName) {
		branchSources {
			git {
				credentialsId("git_jenkins")
				includes(branchPattern)
				remote (gitBaseUrl + gitRepository)
			}
		}
		triggers {
			periodic(5)
		}
		orphanedItemStrategy {
			discardOldItems {
				daysToKeep(30)
			}
		}
	}
}
