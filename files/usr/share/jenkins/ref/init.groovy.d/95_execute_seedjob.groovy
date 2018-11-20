#!groovy

import hudson.model.*;

// define a pattern, which jobs I do not want to run
def jobName = "SeedJob"

// get all jobs which exists
jobs = Hudson.instance.getAllItems(FreeStyleProject)

// iterate through the jobs
seedJob = jobs.find { it.getName() == jobName }

println("--- Reset Seed Job")
seedJob.getBuilds().each { it.delete() }
seedJob.nextBuildNumber = 1
seedJob.save()

println("--- Rerun Seed Job")
seedJob.scheduleBuild(0, null)
