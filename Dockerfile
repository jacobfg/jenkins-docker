# FROM jenkins/jenkins:lts
# FROM jenkins/jenkins:2.138.3
FROM jenkins/jenkins:2.150.1

USER root

COPY /files/usr/local/bin/generate-self-signed-ssl /usr/local/bin/generate-self-signed-ssl

RUN mkdir /etc/ssl/jenkins ; \
    chown jenkins:jenkins -R /etc/ssl/jenkins ; \
    sed -i "2i /usr/local/bin/generate-self-signed-ssl \"${CERTIFICATE_SUBJECT:-localhost}\"" /usr/local/bin/jenkins.sh

USER jenkins

# COPY /files/ /
COPY /files/usr/share/jenkins/ref/plugins.txt /usr/share/jenkins/ref/plugins.txt

RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state ; \
    /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

ENV JAVA_OPTS '-Djenkins.install.runSetupWizard=false'
ENV JENKINS_OPTS --httpPort=-1 --httpsPort=8443 --httpsCertificate=/etc/ssl/jenkins/ssl-cert-snakeoil.pem --httpsPrivateKey=/etc/ssl/jenkins/ssl-cert-snakeoil.key
EXPOSE 8443

COPY /files/usr/share/jenkins/ref/init.groovy.d/*.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY /files/usr/share/jenkins/ref/plugins/*.jpi /usr/share/jenkins/ref/plugins/

# mkdir data
# cat > data/log.properties <<EOF
# handlers=java.util.logging.ConsoleHandler
# jenkins.level=FINEST
# java.util.logging.ConsoleHandler.level=FINEST
# EOF
# docker run --name myjenkins -p 8080:8080 -p 50000:50000 --env JAVA_OPTS="-Djava.util.logging.config.file=/var/jenkins_home/log.properties" -v `pwd`/data:/var/jenkins_home jenkins/jenkins:lts
