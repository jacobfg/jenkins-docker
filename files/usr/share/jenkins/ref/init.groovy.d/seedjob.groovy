#!groovy

import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import jenkins.model.Jenkins

def jobName = "SeedJob"

seedJob = Jenkins.instance.getItem(jobName)

while(true) {
  if (seedJob) break
  println("--- Waiting for Seed Job to be created")
  sleep(1000)
  // update status info
  seedJob = Jenkins.instance.getItem(jobName)
}

println("--- Create Seed Job")
seedJob.scheduleBuild(0, null)

seedJob = Jenkins.instance.getItem(jobName)

def running = (seedJob.lastBuild != null) ? seedJob.lastBuild.building : true
// wait for job builds to complete
while(true) {
  if (!running) break
  // println("--- Waiting for Seed Job to finish")
  sleep(60000)
  // update status info
  seedJob = Jenkins.instance.getItem(jobName)
  running = seedJob.lastBuild.building
}

ScriptApproval sa = ScriptApproval.get();

// approve scripts
for (ScriptApproval.PendingScript pending : sa.getPendingScripts()) {
  println("--- Approve Pending Scripts")
  sa.approveScript(pending.getHash());
}

println("--- Clean Seed Job Build History")
seedJob.getBuilds().each { it.delete() }
seedJob.nextBuildNumber = 1
seedJob.save()

println("--- Rerunning Seed Job, Scripts Approved")
seedJob.scheduleBuild(0, null)
