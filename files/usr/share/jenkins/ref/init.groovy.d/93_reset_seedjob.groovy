#!groovy

import com.cloudbees.hudson.plugins.folder.AbstractFolder
import jenkins.model.Jenkins
import hudson.model.AbstractItem

// change this variable to match the name of the job whose builds you want to delete
def jobName = "SeedJob"
// Set to true in order to reset build number to 1
def resetBuildNumber = true

def removeBuilds(job, resetBuildNumber) {
  if (job instanceof AbstractFolder) {
    for (subJob in job.getItems()) {
      removeBuilds(subJob, resetBuildNumber)
    }
  } else if (job instanceof AbstractItem) {
    job.getBuilds().each { it.delete() }
    if (resetBuildNumber) {
      job.nextBuildNumber = 1
      job.save()
    }
  } else {
    throw new RuntimeException("Unsupported job type ${job.getClass().getName()}!")
  }
}


removeBuilds(Jenkins.instance.getItem(jobName), resetBuildNumber)
