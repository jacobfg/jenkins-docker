FROM jenkins/jenkins:lts
# FROM jenkins/jenkins:2.138.3

USER root

COPY /files/ /

RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state ; \
    /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt ; \
    mkdir /etc/ssl/jenkins ; \
    chown jenkins:jenkins -R /etc/ssl/jenkins ; \
    sed -i "2i /usr/local/bin/generate-self-signed-ssl \"${CERTIFICATE_SUBJECT:-localhost}\"" /usr/local/bin/jenkins.sh

ENV JAVA_OPTS '-Djenkins.install.runSetupWizard=false'
ENV JENKINS_OPTS --httpPort=-1 --httpsPort=8443 --httpsCertificate=/etc/ssl/jenkins/ssl-cert-snakeoil.pem --httpsPrivateKey=/etc/ssl/jenkins/ssl-cert-snakeoil.key
EXPOSE 8443

# mkdir data
# cat > data/log.properties <<EOF
# handlers=java.util.logging.ConsoleHandler
# jenkins.level=FINEST
# java.util.logging.ConsoleHandler.level=FINEST
# EOF
# docker run --name myjenkins -p 8080:8080 -p 50000:50000 --env JAVA_OPTS="-Djava.util.logging.config.file=/var/jenkins_home/log.properties" -v `pwd`/data:/var/jenkins_home jenkins/jenkins:lts

USER jenkins
