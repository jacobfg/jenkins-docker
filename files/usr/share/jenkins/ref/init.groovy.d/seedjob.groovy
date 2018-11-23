#!groovy

import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import jenkins.model.Jenkins

def jobName = "SeedJob"

seedJob = Jenkins.instance.getItem(jobName)

while(true) {
  if (seedJob) break
  println("--> waiting for seed job to be created")
  sleep(1000)
  // update status info
  seedJob = Jenkins.instance.getItem(jobName)
}

println("--> create seed job")
seedJob.scheduleBuild(0, null)

seedJob = Jenkins.instance.getItem(jobName)

def running = (seedJob.lastBuild != null) ? seedJob.lastBuild.building : true
// wait for job builds to complete
while(true) {
  if (!running) break
  // println("--> waiting for seed job to finish")
  sleep(1000)
  // update status info
  seedJob = Jenkins.instance.getItem(jobName)
  running = seedJob.lastBuild.building
}

ScriptApproval sa = ScriptApproval.get();

// approve scripts
for (ScriptApproval.PendingScript pending : sa.getPendingScripts()) {
  println("--> approve pending scripts")
  sa.approveScript(pending.getHash());
}

println("--> clean seed job build history")
seedJob.getBuilds().each { it.delete() }
seedJob.nextBuildNumber = 1
seedJob.save()

println("--> rerunning seed job, scripts approved")
seedJob.scheduleBuild(0, null)
