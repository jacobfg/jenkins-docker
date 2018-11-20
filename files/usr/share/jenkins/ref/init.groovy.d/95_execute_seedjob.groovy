#!groovy

import hudson.model.*;

// define a pattern, which jobs I do not want to run
def jobName = "SeedJob"

// get all jobs which exists
jobs = Hudson.instance.getAllItems(FreeStyleProject)

// iterate through the jobs
for (j in jobs) {
  println "Starting ${j.getName()}"

  if (j.getName() == jobName) {
    println "Starting SeedJob"
    // first check, if job is buildable
    // if (j instanceof BuildableItem) {
      // run that job
      j.scheduleBuild(0, null)
    // }
  }

}
