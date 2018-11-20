#!groovy

import org.jenkinsci.plugins.scriptsecurity.scripts.*
ScriptApproval sa = ScriptApproval.get();

// approve scripts
for (ScriptApproval.PendingScript pending : sa.getPendingScripts()) {
  println("--- Approve Pending Scripts")
  sa.approveScript(pending.getHash());
}

// approve signatures
for (ScriptApproval.PendingSignature pending : sa.getPendingSignatures()) {
  println("--- Approve Pending Signatures")
 	sa.approveSignature(pending.signature);
}
