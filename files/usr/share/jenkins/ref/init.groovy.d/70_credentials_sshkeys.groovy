#!groovy

// https://github.com/samrocketman/jenkins-bootstrap-shared/blob/master/scripts/credentials-multitype.groovy
// http://tdongsi.github.io/blog/2017/12/30/groovy-hook-script-and-jenkins-configuration-as-code/

import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import hudson.util.Secret
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import groovy.json.JsonSlurper

class CredentialsConfig {
  /**
    Resolves a credential scope if given a string.
    */
  static void resolveScope(String scope) {
      scope = scope.toString().toUpperCase()
      if(!(scope in ['GLOBAL', 'SYSTEM'])) {
          scope = 'GLOBAL'
      }
      CredentialsScope."${scope}"
  }

  /**
    A shared method used by other "setCredential" methods to safely create a
    credential in the global domain.
    */
  static void addCredential(String credentials_id, def credential) {
      boolean modified_creds = false
      Domain domain
      SystemCredentialsProvider system_creds = SystemCredentialsProvider.getInstance()
      Map system_creds_map = system_creds.getDomainCredentialsMap()
      domain = (system_creds_map.keySet() as List).find { it.getName() == null }
      if(!system_creds_map[domain] || (system_creds_map[domain].findAll {credentials_id.equals(it.id)}).size() < 1) {
          if(system_creds_map[domain] && system_creds_map[domain].size() > 0) {
              //other credentials exist so should only append
              system_creds_map[domain] << credential
          }
          else {
              system_creds_map[domain] = [credential]
          }
          modified_creds = true
      }
      //save any modified credentials
      if(modified_creds) {
          println "${credentials_id} credentials added to Jenkins."
          system_creds.setDomainCredentialsMap(system_creds_map)
          system_creds.save()
      }
      else {
          println "Nothing changed.  ${credentials_id} credentials already configured."
      }
  }

  /**
    Supports SSH username and private key (directly entered private key)
    credential provided by BasicSSHUserPrivateKey class.
    Example:
      [
          'credential_type': 'BasicSSHUserPrivateKey',
          'credentials_id': 'some-credential-id',
          'description': 'A description of this credential',
          'user': 'some user',
          'key_passwd': 'secret phrase',
          'key': '''
  private key contents (do not indent it)
          '''.trim()
      ]
    */
  static void setBasicSSHUserPrivateKey(Map settings) {
      String credentials_id = ((settings['credentials_id'])?:'').toString()
      String user = ((settings['user'])?:'').toString()
      String key = ((settings['key'])?:'').toString()
      String key_passwd = ((settings['key_passwd'])?:'').toString()
      String description = ((settings['description'])?:'').toString()

      addCredential(
              credentials_id,
              new BasicSSHUserPrivateKey(
                  resolveScope(settings['scope']),
                  credentials_id,
                  user,
                  new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(key),
                  key_passwd,
                  description)
              )
  }

  /**
    Supports String credential provided by StringCredentialsImpl class.
    Example:
      [
          'credential_type': 'StringCredentialsImpl',
          'credentials_id': 'some-credential-id',
          'description': 'A description of this credential',
          'secret': 'super secret text'
      ]
    */
  static void setStringCredentialsImpl(Map settings) {
      String credentials_id = ((settings['credentials_id'])?:'').toString()
      String description = ((settings['description'])?:'').toString()
      String secret = ((settings['secret'])?:'').toString()
      addCredential(
              credentials_id,
              new StringCredentialsImpl(
                  resolveScope(settings['scope']),
                  credentials_id,
                  description,
                  Secret.fromString(secret))
              )
  }

  /**
    Supports username and password credential provided by
    UsernamePasswordCredentialsImpl class.
    Example:
      [
          'credential_type': 'UsernamePasswordCredentialsImpl',
          'credentials_id': 'some-credential-id',
          'description': 'A description of this credential',
          'user': 'some user',
          'password': 'secret phrase'
      ]
    */
  static void setUsernamePasswordCredentialsImpl(Map settings) {
      String credentials_id = ((settings['credentials_id'])?:'').toString()
      String user = ((settings['user'])?:'').toString()
      String password = ((settings['password'])?:'').toString()
      String description = ((settings['description'])?:'').toString()

      addCredential(
              credentials_id,
              new UsernamePasswordCredentialsImpl(
                  resolveScope(settings['scope']),
                  credentials_id,
                  description,
                  user,
                  password)
              )
  }
}


// lets load the credentials
try {
  def text = new File("/run/secrets/jenkins-config").text
  def json = new JsonSlurper().parseText(text)
  def allowedTypes = CredentialsConfig.metaClass.methods*.name.toSet()

  //iterate through credentials and add them to Jenkins
  json['credentials'].each {
    type = "set${(it['credential_type'])?:'empty credential_type'}".toString()

    if (type in allowedTypes) {
      println "--- Adding Credential ${it['credentials_id']}"
      CredentialsConfig.invokeMethod(type,it)
    }
    else {
      println "--- WARNING: Unknown credential type: ${type}. Nothing changed."
    }
  }
} catch (ex) {
  println "--- ERROR: ${ex}"
}
