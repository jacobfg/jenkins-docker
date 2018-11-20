#!groovy

import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
import jenkins.security.s2m.AdminWhitelistRule
import org.kohsuke.stapler.StaplerProxy
import hudson.tasks.Mailer

def jlc = JenkinsLocationConfiguration.get()

println("--- Configuring Remoting (JNLP4 only, no Remoting CLI)")
Jenkins.instance.getDescriptor("jenkins.CLI").get().setEnabled(false)
Jenkins.instance.agentProtocols = new HashSet<String>(["JNLP4-connect"])

println("--- Enable Slave -> Master Access Control")
Jenkins.instance.getExtensionList(StaplerProxy.class)
    .get(AdminWhitelistRule.class)
    .masterKillSwitch = false

println("--- Checking the CSRF protection")
if (Jenkins.instance.crumbIssuer == null) {
    println "CSRF protection is disabled, Enabling the default Crumb Issuer"
    Jenkins.instance.crumbIssuer = new DefaultCrumbIssuer(true)
}

println("--- Configuring Quiet Period")
// We do not wait for anything
Jenkins.instance.quietPeriod = 0
Jenkins.instance.save()

println("--- Configuring Email global settings")
jlc.adminAddress = "admin@non.existent.email"
Mailer.descriptor().defaultSuffix = "@non.existent.email"

def Root_URL = System.getenv('JENKINS_ROOT_URL')
println("--- Configuring Root URL: ${Root_URL}")
jlc.setUrl(Root_URL)
jlc.save()
