#!groovy

import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval

ScriptApproval sa = ScriptApproval.get();

// approve scripts
for (ScriptApproval.PendingScript pending : sa.getPendingScripts()) {
  println("--- Approve Pending Scripts")
  sa.approveScript(pending.getHash());
}
