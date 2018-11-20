#!groovy

import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import groovy.json.JsonSlurper

instance = Jenkins.getInstance()
globalNodeProperties = instance.getGlobalNodeProperties()
envVarsNodePropertyList = globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)

println("--- Setting Global Variables")

// skip if missing or parse error
try {
  def text = new File("/run/secrets/jenkins-config").text
  def json = new JsonSlurper().parseText(text)

  envVars = null
  if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
    newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
    globalNodeProperties.add(newEnvVarsNodeProperty)
    envVars = newEnvVarsNodeProperty.getEnvVars()
  } else {
    envVars = envVarsNodePropertyList.get(0).getEnvVars()
  }

  json['global_variables'].each {
    println("--- Adding ${it['name']}")
    envVars.put(it["name"], it["value"])
  }

  instance.save()
} catch (ex) {
  println "ERROR: ${ex}"
  println configXml.stripIndent()
}
