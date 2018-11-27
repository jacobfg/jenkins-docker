#!/bin/bash -eu

USER="admin"
PASS=$(cat config/adminpw.txt)
JENKINS_HOST="${USER}:${PASS}@localhost:8443"
curl -ksSL "https://$JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" \
  | perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1 \2\n/g' \
  | sed 's/ /:/' \
  | sort \
  > files/usr/share/jenkins/ref/plugins.txt
