#!groovy

import jenkins.model.JenkinsLocationConfiguration

def Root_URL = System.getenv('JENKINS_ROOT_URL')

jlc = new jenkins.model.JenkinsLocationConfiguration()

jlc = JenkinsLocationConfiguration.get()
jlc.setUrl(Root_URL)
jlc.save()
