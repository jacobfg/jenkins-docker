#!groovy

import hudson.model.*;

// define a pattern, which jobs I do not want to run
def jobName = "SeedJob"

// get all jobs which exists
jobs = Hudson.instance.getAllItems(FreeStyleProject)

// iterate through the jobs
job = (j in jobs).find { j.getName() == jobName }

println("--- Reset Seed Job")
job.getBuilds().each { it.delete() }
if (resetBuildNumber) {
  job.nextBuildNumber = 1
  job.save()
}

println("--- Rerun Seed Job")
job.scheduleBuild(0, null)
