#!groovy

import jenkins.model.Jenkins

Jenkins.instance.setNumExecutors(2)
