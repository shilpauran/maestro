#!/usr/bin/env groovy

@Library(['piper-lib', 'piper-lib-os']) _
@Library('txs-lib@v2.2.3')
import com.sap.jenkins.taxservice.PipelineParameters
import com.sap.jenkins.taxservice.Gitflow

Gitflow flow = new Gitflow(this)
PipelineParameters txsPipeline = new PipelineParameters(this)
milestonePipelineId = '16e3c36c-8768-47d2-98af-fd3a1c790399'
releasePipelineId = '7259c2d8-8859-4a23-9695-2e2fd237c477'

if (flow.isCommit()?.toMaster()) {
	properties([
		pipelineTriggers([cron('H 3 * * 1')])
	])
}else if (flow.isCommit()?.toDevelop()) {
	properties([
		pipelineTriggers([cron('H 3 * * 1-5')])
	])
}

try {
	flowSetupStage(script: this, projectName: 'com.sap.slh.tax-tax-maestro')

	flowLevel1Stage('Build') {
		node {
			measureDuration(script: this, measurementName: 'build_duration') {
				setVersion (
					script: this,
					buildTool: 'maven'
				)

				stashFiles(script: this) {
					executeBuild script: this, buildType: 'xMakeStage', xMakeBuildQuality: txsPipeline.getBuildQuality().toString()
				}

				testsPublishResults (
					script: this,
					junit: [updateResults: true, archive: true],
					jacoco: [archive: true],
					allowUnstableBuilds: false
				)
				cucumber ( fileIncludePattern: '**/cucumber.json' )

				stash includes: 'cumulus-configuration.json', name: 'cumulus-config'
				stash includes: '**/target/cucumber.json', name: 'cumulus-cucumber'
				stash includes: '**/TEST*.xml', name: 'cumulus-junit'
			}
		}
	}

	flowLevel2Stage('Static Code Checks') {
		parallel(
			'Sonar': {
				node {
					executeSonarScan(
						script: this,
						useWebhook: true,
						options: "-Dsonar.projectKey=${txsPipeline.getSonarProjectKey()} -Dsonar.projectVersion=${this.globalPipelineEnvironment.getArtifactVersion()?.tokenize('-')?.get(0)}"
					)
					timeout(time: 20, unit: 'MINUTES') {
						def qg = waitForQualityGate();
						if (qg.status != 'OK' && qg.status != 'WARN') {
						error "Pipeline aborted due to quality gate failure: ${qg.status}"
						}
					}
				}
			},
			'Fortify': {
				node {
					deleteDir()
					executeFortifyScan (
						script: this,
						environment: 'docker',
						fortifyProjectName: txsPipeline.getProjectName(),
						fortifyProjectVersion: txsPipeline.getFortifyProjectVersion()
					)
				}
			}, failFast: false
		)
	}

	flowLevel2Stage('E2E System Tests', unstableOnError: true) {
		node {
			deleteDir()
			downloadArtifactsFromNexus (
				script: this,
				fromStaging: true,
				buildDescriptorFile: 'tax-maestro-web/pom.xml',
				disableLegacyNaming: true
			)
			githubPublishRelease (
				script: this,
				version: "build_${globalPipelineEnvironment.getArtifactVersion()}",
				assetPath: "$env.WORKSPACE/tax-maestro-web/target/tax-maestro-web.jar"
			)
		}

		parallel(
			'Acceptance': {
				node {
					deleteDir()
					def result = build job: '../txs-integration-job/master',
						propagate: true,
						wait: true,
						parameters: [
							string(name: 'caller_job', value: "${env.BUILD_TAG}".toString()),
							string(name: 'service_name', value: "tax-maestro"),
							string(name: 'service_version', value: globalPipelineEnvironment.getArtifactVersion()),
							string(name: 'build_quality', value: txsPipeline.getBuildQuality().toString().toUpperCase()),
							string(name: 'test_type', value: getAcceptanceTestType())
						]
					echo("Acceptance tests result: $result")
				}
			},
			'Performance': {
				node {
					deleteDir()
					def result = build job: '../txs-integration-job/master',
						propagate: false,
						wait: true,
						parameters: [
							string(name: 'caller_job', value: "${env.BUILD_TAG}".toString()),
							string(name: 'service_name', value: "tax-maestro"),
							string(name: 'service_version', value: globalPipelineEnvironment.getArtifactVersion()),
							string(name: 'build_quality', value: txsPipeline.getBuildQuality().toString().toUpperCase()),
							string(name: 'test_type', value: 'PERFORMANCE'),
							string(name: 'test_scope', value: 'APPLICATION')
						]
					echo("Performance tests result: $result")
				}
			}, failFast: false
		)
	}

	flowLevel2Stage('Dependencies Compliance', unstableOnError: flow.isLevel2Stage()) {
		parallel(
			'IPScan and PPMS': {
				node {
					deleteDir()
					unstash 'buildDescriptor'
					def wsProjectNames = txsPipeline.getWhiteSourceProjectNamesFromMaven()

					executeWhitesourceScan(
						script: this,
						scanType: 'maven',
						whitesourceProjectNames: wsProjectNames
					)
					executePPMSComplianceCheck(
						script: this,
						scanType: 'whitesource',
						whitesourceProjectNames: wsProjectNames,
						ppmsBuildVersionCreation: true,
						ppmsBuildVersion: "\${version.major}.\${version.minor}.0",
						ppmsChangeRequestUpload: true
					)
				}
			},
			'Vulas': {
				node {
					deleteDir()
					executeVulasScan script: this, scanType: 'maven'
				}
			}, failFast: false
		)
	}

	flowLevel2Stage('Cumulus Upload') {
		node {
			deleteDir()
			String version = this.globalPipelineEnvironment.getArtifactVersion()
			String pipelineId = getPipelineId()

			unstash 'cumulus-config'
			sapCumulusUpload (
					script: this,
					version: version,
					pipelineId: pipelineId,
					filePattern: "cumulus-configuration.json",
					stepResultType: "cumulus-upload"
			)

			unstash 'cumulus-cucumber'
			sapCumulusUpload (
					script: this,
					version: version,
					pipelineId: pipelineId,
					filePattern: "**/target/cucumber.json",
					stepResultType: "cucumber"
			)

			unstash 'cumulus-junit'
			sapCumulusUpload (
					script: this,
					version: version,
					pipelineId: pipelineId,
					filePattern: "**/TEST*.xml",
					stepResultType: "junit"
			)
		}
	}

	flowPromoteStage(script: this)

	flowDeployStage(
			script: this,
			manifestPath: 'tax-maestro-web',
			credentialsId: 'CF_USER'
	)

} catch (Throwable err) {
	globalPipelineEnvironment.addError(this, err)
	if (flow.isLevel2Stage() || flow.isLevel3Stage())
		slackSendNotification (
			script: this,
			channel: '#cis-voyager-team',
			credentialsId: 'voyager_slack_notify'
		)
	throw err
}

def getPipelineId() {
	PipelineParameters txsPipeline = new PipelineParameters(this)

	if (txsPipeline.getBuildQuality().isRelease()) {
		return releasePipelineId
	}
	return milestonePipelineId
}

def getAcceptanceTestType() {
	Gitflow flow = new Gitflow(this)

	if (flow.isCommit()?.toMaster() || flow.isPullRequest()?.toMaster()) {
		return "ACCEPTANCE_MASTER"
	} else if (flow.isCommit()?.toDevelop() || flow.isPullRequest()?.toDevelop()) {
		return "ACCEPTANCE_DEV"
	} else if (flow.isCommit()?.toRelease() || flow.isPullRequest()?.toRelease()) {
		return "ACCEPTANCE_RELEASE"
	}
}
