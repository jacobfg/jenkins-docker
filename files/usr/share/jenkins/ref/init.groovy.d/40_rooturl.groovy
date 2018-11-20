#!groovy

import jenkins.model.JenkinsLocationConfiguration

def Root_URL = System.getenv('JENKINS_ROOT_URL')

println("--- Set Jenkins Root URL: ${Root_URL}")
jlc = JenkinsLocationConfiguration.get()
jlc.setUrl(Root_URL)
jlc.save()
