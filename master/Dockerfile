FROM visualphoenix/alpine-oracle-jdk:8u45-b14-1

ARG JENKINS_VERSION
ARG JENKINS_SHA
ARG JENKINS_SLAVE_AGENT_PORT
ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000

ENV JENKINS_HOME /jenkins
ENV JENKINS_SLAVE_AGENT_PORT ${JENKINS_SLAVE_AGENT_PORT:-50000}
ENV COPY_REFERENCE_FILE_LOG $JENKINS_HOME/copy_reference_file.log

VOLUME /jenkins

RUN apk update && apk add curl bind-tools bash zip unzip \
 && mkdir -p /usr/share/jenkins/ref

RUN addgroup -g ${gid} ${group} \
 && adduser -h "$JENKINS_HOME" -u ${uid} -G ${group} -s /bin/sh -D ${user}

RUN TINI_VERSION=0.10.0 \
 && TINI_SHA=7d00da20acc5c3eb21d959733917f6672b57dabb \
 && wget https://github.com/krallin/tini/releases/download/v${TINI_VERSION}/tini-static -O /bin/tini \
 && chmod +x /bin/tini \
 && echo "$TINI_SHA  /bin/tini" | sha1sum -c -

RUN JENKINS_VERSION=${JENKINS_VERSION:-2.9} \
 && JENKINS_SHA=${JENKINS_SHA:-1fd02a942cca991577ee9727dd3d67470e45c031} \
 && wget https://repo.jenkins-ci.org/public/org/jenkins-ci/main/jenkins-war/${JENKINS_VERSION}/jenkins-war-${JENKINS_VERSION}.war \
         -O /usr/share/jenkins/jenkins.war \
 && echo "$JENKINS_SHA  /usr/share/jenkins/jenkins.war" | sha1sum -c -

EXPOSE 8080
EXPOSE ${JENKINS_SLAVE_AGENT_PORT:-50000}

COPY jenkins/jenkins-support /usr/local/bin/jenkins-support
COPY jenkins/install-plugins.sh /usr/local/bin/install-plugins.sh

ENV JENKINS_UC https://updates.jenkins.io
RUN PATH=/usr/lib/jvm/java-8-oracle/bin:$PATH \
    install-plugins.sh \
    cas-plugin ssh-credentials mailer \
    matrix-auth sonar email-ext git \
    maven-plugin metrics credentials \
    cloudbees-folder rebuild  workflow-aggregator \
    ldap swarm

COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY init.groovy.d /usr/share/jenkins/ref/init.groovy.d
COPY init.groovy.mixins /usr/share/jenkins/ref/init.groovy.mixins

ENTRYPOINT ["/docker-entrypoint.sh"]
