FROM jenkins/jenkins:lts

USER root
COPY /files/ /

RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state ; \
    /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

ENV JAVA_OPTS '-Djenkins.install.runSetupWizard=false'

# COPY https.pem /var/lib/jenkins/cert
# COPY https.key /var/lib/jenkins/pk
# ENV JENKINS_OPTS --httpPort=-1 --httpsPort=8083 --httpsCertificate=/var/lib/jenkins/cert --httpsPrivateKey=/var/lib/jenkins/pk
# EXPOSE 8083


# mkdir data
# cat > data/log.properties <<EOF
# handlers=java.util.logging.ConsoleHandler
# jenkins.level=FINEST
# java.util.logging.ConsoleHandler.level=FINEST
# EOF
# docker run --name myjenkins -p 8080:8080 -p 50000:50000 --env JAVA_OPTS="-Djava.util.logging.config.file=/var/jenkins_home/log.properties" -v `pwd`/data:/var/jenkins_home jenkins/jenkins:lts


USER jenkins
