#!groovy

import jenkins.model.Jenkins
import hudson.model.FreeStyleProject

// define a pattern, which jobs I do not want to run
def jobName = "SeedJob"

// get all jobs which exists
def jobs = Jenkins.instance.getAllItems(FreeStyleProject)

// iterate through the jobs
def seedJob = Jenkins.instance.getItem(jobName)

println("--- Clean Seed Job Build History")
seedJob.getBuilds().each { it.delete() }
seedJob.nextBuildNumber = 1
seedJob.save()

println("--- Rerunning Seed Job, Scripts Approved")
seedJob.scheduleBuild(0, null)
